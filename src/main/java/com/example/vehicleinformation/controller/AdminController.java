package com.example.vehicleinformation.controller;

import com.example.vehicleinformation.model.UserRights;
import com.example.vehicleinformation.service.UserRightsService;
import com.example.vehicleinformation.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final VehicleService vehicleService;
    private final UserRightsService userRightsService;

    public AdminController(VehicleService vehicleService, UserRightsService userRightsService) {
        this.vehicleService = vehicleService;
        this.userRightsService = userRightsService;
    }

    @GetMapping({"/dashboard", "", "/"})
    public String dashboard(Model model) {
        model.addAttribute("stats", vehicleService.getDashboardStats());
        return "admin-dashboard";
    }

    @GetMapping("/user-rights")
    public String userRightsPage(Model model) {
        UserRights rights = userRightsService.getRights();
        model.addAttribute("allowEdit", rights.isAllowEdit());
        model.addAttribute("allowDelete", rights.isAllowDelete());
        return "user-rights";
    }

    @PostMapping("/user-rights")
    public String updateUserRights(@RequestParam(defaultValue = "false") boolean allowEdit,
                                   @RequestParam(defaultValue = "false") boolean allowDelete,
                                   RedirectAttributes redirectAttributes) {
        userRightsService.update(allowEdit, allowDelete);
        redirectAttributes.addFlashAttribute("successMessage",
                "User rights updated. Edit: " + (allowEdit ? "ON" : "OFF")
                        + ", Delete: " + (allowDelete ? "ON" : "OFF") + ".");
        return "redirect:/admin/user-rights";
    }
}
