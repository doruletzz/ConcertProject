package app.service;

import app.model.Concert;
import app.model.Hall;
import app.model.Sale;
import app.model.SeatSale;
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
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.lock();
                for(Concert c:concertRepository.findAll())
                    updateConcertTotalAmount(c);

                lock.unlock();
            }
        }
    }

    private void updateConcertTotalAmount(Concert concert){
        List<Sale> sales = saleRepository.findSalesByConcert(concert);
        BigDecimal sum = new BigDecimal(0);
        for(Sale s : sales)
            sum = sum.add(s.getAmount());

        if(!concert.getTotalAmount().equals(sum)) {
            System.out.println("TOTAL AMOUNT CHANGED OF: " +concert.toString() + " TO " + sum.toString());
            concert.setTotalAmount(sum);
            concertRepository.save(concert);
        }
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
                return "Could not save. Concert was not found";
            }

            //TODO: Change lock on all the Service to lock on each Concert
            lock.lock();

            if(findOneInRepo(concert.get(), seats)){
                lock.unlock();
                return "Could Not Save. Seats are already taken.";
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

            return "Saved";
        }
    }

    @Async
    public Future<String> addSale(Long concertId, Long numSeats, List<Long> seats) throws ExecutionException, InterruptedException {
        //TODO: Create Check Entity for persitance of each operation and it's result
        return threadPool.execute(new SaleWorker(concertId,seats));

    }



}
