//package app.controller;
//
//import app.model.Customer;
//import app.repos.CustomerRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//@CrossOrigin
//@RequestMapping(path="/demo")
//public class MainController {
//    @Autowired
//    private CustomerRepository customerRepository;
//
//    @PostMapping(path="/add")
//    public @ResponseBody
//    String addNewUser (@RequestParam String name
//            , @RequestParam String email) {
//        // @ResponseBody means the returned String is the response, not a view name
//        // @RequestParam means it is a parameter from the GET or POST request
//
//        Customer n = new Customer();
//        n.setFirstName(name);
//        n.setLastName(email);
//        customerRepository.save(n);
//        return "Saved";
//    }
//
//    @GetMapping(path="/all")
//    public @ResponseBody Iterable<Customer> getAllUsers() {
//        // This returns a JSON or XML with the users
//        return customerRepository.findAll();
//    }
//}