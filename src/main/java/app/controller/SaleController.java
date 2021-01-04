package app.controller;

import app.model.Concert;
import app.model.Sale;
import app.model.SeatSale;
import app.repos.ConcertRepository;
import app.repos.SaleRepository;
import app.repos.SeatSaleRepository;
import app.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Controller
@CrossOrigin
@RequestMapping(path="/sale")
public class SaleController {
    @Autowired
    private SaleService saleService;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private SeatSaleRepository seatSaleRepository;

    @Autowired
    private SaleRepository saleRepository;

    @PostMapping(path="/add")
    public @ResponseBody
    String addSale (@RequestParam(value="concert", required = false) Long concertId
            , @RequestParam(value="num", required = false) Long numSeats
            , @RequestParam(value="seat", required = false) List<Long> seats) {

        Optional<Concert> concert = concertRepository.findById(concertId);
        if(!concert.isPresent())
            return "Could not save. Concert was not found";

        return saleService.addSale(concert.get(), numSeats, seats);
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<Sale> getAllSales() {
        // This returns a JSON or XML with the users
        return saleRepository.findAll();
    }
}