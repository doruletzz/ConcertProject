package app.controller;

import app.model.Concert;
import app.model.Hall;
import app.model.Sale;
import app.model.SeatSale;
import app.repos.ConcertRepository;
import app.repos.HallRepository;
import app.repos.SaleRepository;
import app.repos.SeatSaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.NumberFormat;
import java.util.*;

@Controller
@CrossOrigin
@RequestMapping(path="/concert")
public class ConcertController {
    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @PostMapping(path="/add")
    public @ResponseBody
    String addConcert (@RequestParam(value="hall", required = false) Long hallId
            , @RequestParam(value="title", required = false) String title
            , @RequestParam(value="price", required = false) BigDecimal price
            , @RequestParam(value="total", required = false) BigDecimal totalAmount) {

        Optional<Hall> hall =hallRepository.findById(hallId);
        if(!hall.isPresent())
            return "Not Saved, Hall not found";

        Concert concert = new Concert();
        concert.setHall(hall.get());
        concert.setDateShow(new Date(Calendar.getInstance().getTime().getTime()));
        concert.setTitle(title);
        concert.setPrice(price);
        concert.setTotalAmount(totalAmount);

        concertRepository.save(concert);
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<Concert> getAllSales() {
        // This returns a JSON or XML with the users
        return concertRepository.findAll();
    }
}