package com.estudo_bd.api.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {
    
    @GetMapping("/")
    public String getHome() {
        return "home";
    }

}
