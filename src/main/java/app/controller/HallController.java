package app.controller;

import app.model.Concert;
import app.model.Hall;
import app.repos.ConcertRepository;
import app.repos.HallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.Calendar;
import java.util.Currency;
import java.util.Optional;

@Controller
@CrossOrigin
@RequestMapping(path="/hall")
public class HallController {
    @Autowired
    private HallRepository hallRepository;

    @PostMapping(path="/add")
    public @ResponseBody
    String addConcert (@RequestParam(value="nrSeats", required = false) Long num) {
        Hall hall = new Hall();
        hall.setNrSeats(num);
        hallRepository.save(hall);
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<Hall> getAllSales() {
        // This returns a JSON or XML with the users
        return hallRepository.findAll();
    }
}