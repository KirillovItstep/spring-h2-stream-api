package org.itstep.springh2streamapi.controller;

import org.itstep.springh2streamapi.repo.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomerController {
    @Autowired
    CustomerRepo repo;

    @RequestMapping("/")
    public String home(){
        return "index.html";
    }

}
