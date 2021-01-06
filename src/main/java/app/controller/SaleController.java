package app.controller;

import app.model.CheckData;
import app.model.Concert;
import app.model.Sale;
import app.model.SeatSale;
import app.repos.CheckDataRepository;
import app.repos.ConcertRepository;
import app.repos.SaleRepository;
import app.repos.SeatSaleRepository;
import app.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Controller
@CrossOrigin
@RequestMapping(path="/sale")
public class SaleController {
    @Autowired
    private SaleService saleService;

    @Autowired
    private CheckDataRepository checkDataRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SeatSaleRepository seatSaleRepository;

    @PostMapping(path="/add")
    public @ResponseBody
    String addSale (@RequestParam(value="concert", required = false) Long concertId
            , @RequestParam(value="num", required = false) Long numSeats
            , @RequestParam(value="seat", required = false) List<Long> seats) throws ExecutionException, InterruptedException {

        String response = saleService.addSale(concertId, numSeats, seats).get();
        CheckData checkData = new CheckData();
        checkData.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        checkData.setTime(new Time(Calendar.getInstance().getTime().getTime()));
        checkData.setResponse(response);
        checkDataRepository.save(checkData);
        return response;
    }

    @GetMapping(path="/all_sales")
    public @ResponseBody Iterable<SeatSale> getAllSeatSales(@RequestParam(value="concert", required = false) Long concertId) {
        // This returns a JSON or XML with the users
        return seatSaleRepository.findAllByConcertId(concertId);
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<Sale> getAllSales() {
        // This returns a JSON or XML with the users
        return saleRepository.findAll();
    }
}