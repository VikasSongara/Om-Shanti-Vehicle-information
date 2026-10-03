package com.example.vehicleinformation.controller;

import com.example.vehicleinformation.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final VehicleService vehicleService;

    public AdminController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping({"/dashboard", "", "/"})
    public String dashboard(Model model) {
        model.addAttribute("stats", vehicleService.getDashboardStats());
        return "admin-dashboard";
    }
}
