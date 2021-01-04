package app.service;

import app.model.Concert;
import app.model.Sale;
import app.model.SeatSale;
import app.repos.SaleRepository;
import app.repos.SeatSaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SaleService {
    private final ReentrantLock lock = new ReentrantLock();

    //TODO: ADD THREADPOOL

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SeatSaleRepository seatSaleRepository;

    private boolean findOneInRepo(List<Long> seats){
        return !seatSaleRepository.findAllByNrSeats(seats).isEmpty();
    }


    //TODO: this task should be called by the threadpool
    public String addSale(Concert concert, Long numSeats, List<Long> seats){
        //TODO: Change lock on all the Service to lock on each Sale
        lock.lock();
        if(findOneInRepo(seats)){
            lock.unlock();
            return "Could Not Save. Seats are already taken.";
        }

        Sale sale = new Sale();
        sale.setConcert(concert);
        sale.setHall(concert.getHall());
        sale.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        sale.setAmount(concert.getPrice());

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
