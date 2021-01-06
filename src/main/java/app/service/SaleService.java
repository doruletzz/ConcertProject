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
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SaleService {
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

    private final Dictionary<Long, ReentrantLock> lockDictionary = new Hashtable<>();

    private void lockConcert(Concert concert){
        lock.lock();
        if (lockDictionary.get(concert.getIdShow()) != null){
            lockDictionary.get(concert.getIdShow()).lock();
        } else {
            lockDictionary.put(concert.getIdShow(), new ReentrantLock());
            lockDictionary.get(concert.getIdShow()).lock();
        }
        lock.unlock();
    }

    private void unlockConcert(Concert concert){
        lock.lock();
        if (lockDictionary.get(concert.getIdShow()) != null)
            lockDictionary.get(concert.getIdShow()).unlock();
        lock.unlock();
    }

    private class CheckWorker implements Runnable{
        private final Long seconds = 5L;

        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(1000 * seconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Check Worker");

                for(Concert c:concertRepository.findAll())
                    tryUpdateConcertTotalAmount(c);

            }
        }
    }

    @PreDestroy
    public void destroy() {
        threadPool.shutdown();
        System.out.println("Callback triggered - @PreDestroy.");
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
        lockConcert(concert);
//        lock.lock();

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

//        lock.unlock();
        unlockConcert(concert);
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
            System.out.println("Sale Worker");

            Optional<Concert> concert = concertRepository.findById(concertId);
            if(!concert.isPresent()) {
                return "Could not save. Concert was not found: ";
            }

//            lock.lock();
            lockConcert(concert.get());

            if(findOneInRepo(concert.get(), seats)){
//                lock.unlock();
                unlockConcert(concert.get());
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


//            lock.unlock();
            unlockConcert(concert.get());

            return "Saved. Concert: " + concert.get().getTitle();
        }
    }

    @Async
    public Future<String> addSale(Long concertId, Long numSeats, List<Long> seats) throws ExecutionException, InterruptedException {
        return threadPool.execute(new SaleWorker(concertId,seats));

    }



}
