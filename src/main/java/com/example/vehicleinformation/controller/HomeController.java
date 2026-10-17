package com.example.vehicleinformation.controller;

import com.example.vehicleinformation.config.SecurityAuthHelper;
import com.example.vehicleinformation.dto.VehiclePageDto;
import com.example.vehicleinformation.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final VehicleService vehicleService;

    public HomeController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String vehicleType,
                        @RequestParam(required = false) String vehicleNumber,
                        @RequestParam(required = false) String blockNo,
                        @RequestParam(required = false) String houseNo,
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false) String mobileNumber,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "house") String sort,
                        @RequestParam(defaultValue = "asc") String direction,
                        Model model) {
        boolean admin = SecurityAuthHelper.isAdmin();
        // Public users cannot search/filter by admin-only fields.
        if (!admin) {
            vehicleType = null;
            blockNo = null;
            name = null;
            mobileNumber = null;
        }
        VehiclePageDto pageDto = vehicleService.search(
                vehicleType, vehicleNumber, blockNo, houseNo, name, mobileNumber,
                page, size, sort, direction, false
        );
        model.addAttribute("vehiclePage", pageDto);
        return "vehicles";
    }
}
