package app.service;

import app.model.*;
import app.repos.CheckDataRepository;
import app.repos.ConcertRepository;
import app.repos.SaleRepository;
import app.repos.SeatSaleRepository;
import app.utils.MyThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SaleService {
    //TODO: CHANGE from single lock to list of locks for each Concert
    private final ReentrantLock lock = new ReentrantLock();

    private final MyThreadPool<String> threadPool = new MyThreadPool<String>(5);

    private final Thread checkThread = new Thread(new CheckWorker());

    @Autowired
    private CheckDataRepository checkDataRepository;

    @PostConstruct
    void runCheck(){
        checkThread.start();
    }

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SeatSaleRepository seatSaleRepository;

    @Autowired
    private ConcertRepository concertRepository;


    //Checks if any of the seats are already taken
    private boolean findOneInRepo(Concert concert, List<Long> seats){
        return !seatSaleRepository.findAllByNrSeats(concert, seats).isEmpty();
    }

    private class CheckWorker implements Runnable{

        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.lock();
                for(Concert c:concertRepository.findAll())
                    tryUpdateConcertTotalAmount(c);

                lock.unlock();
            }
        }
    }

    private String updateConcertTotalAmount(Concert concert, BigDecimal newTotalAmount){
        if(!concert.getTotalAmount().equals(newTotalAmount)) {
            concert.setTotalAmount(newTotalAmount);
            concertRepository.save(concert);
            return "Incorrect, TOTAL AMOUNT CHANGED OF: " + concert.toString() + " TO " + newTotalAmount.toString() + "FOR: " + concert.getTitle();
        } else {
            return "Correct";
        }

    }


    private void tryUpdateConcertTotalAmount(Concert concert){
        List<Sale> sales = saleRepository.findSalesByConcert(concert);
        BigDecimal sum = new BigDecimal(0);
        for(Sale s : sales)
            sum = sum.add(s.getAmount());

        CheckData checkData = new CheckData();
        checkData.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        checkData.setTime(new Time(Calendar.getInstance().getTime().getTime()));

        String response = updateConcertTotalAmount(concert, sum);
        checkData.setResponse(response);
        checkDataRepository.save(checkData);
    }

    private class SaleWorker implements Callable<String> {
        Long concertId;

        List<Long> seats;

        public SaleWorker(Long concertId, List<Long> seats) {
            this.concertId = concertId;
            this.seats = seats;
        }

        @Override
        public String call(){

            Optional<Concert> concert = concertRepository.findById(concertId);
            if(!concert.isPresent()) {
                return "Could not save. Concert was not found: ";
            }

            //TODO: Change lock on all the Service to lock on each Concert
            lock.lock();

            if(findOneInRepo(concert.get(), seats)){
                lock.unlock();
                return "Could Not Save. Seats are already taken in " + concert.get().getTitle() + ".";
            }

            Sale sale = new Sale();
            sale.setConcert(concert.get());
            sale.setHall(concert.get().getHall());
            sale.setDate(new Date(Calendar.getInstance().getTime().getTime()));
            sale.setAmount(concert.get().getPrice().multiply(BigDecimal.valueOf(seats.size())));

            Sale newSale = saleRepository.save(sale);

            for (Long id: seats){
                SeatSale ss = new SeatSale();
                ss.setSale(newSale);
                ss.setNrSeat(id);
                seatSaleRepository.save(ss);
            }

            lock.unlock();

            return "Saved. Concert: " + concert.get().getTitle();
        }
    }

    @Async
    public Future<String> addSale(Long concertId, Long numSeats, List<Long> seats) throws ExecutionException, InterruptedException {
        //TODO: Create Check Entity for persitance of each operation and it's result
        return threadPool.execute(new SaleWorker(concertId,seats));

    }



}
