package com.example.vehicleinformation.config;

import com.example.vehicleinformation.service.UserRightsService;
import com.example.vehicleinformation.util.BlockOptions;
import com.example.vehicleinformation.util.VehicleTypeOptions;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserRightsService userRightsService;

    public GlobalModelAttributes(UserRightsService userRightsService) {
        this.userRightsService = userRightsService;
    }

    @ModelAttribute("blockOptions")
    public List<String> blockOptions() {
        return BlockOptions.VALUES;
    }

    @ModelAttribute("vehicleTypeOptions")
    public List<String> vehicleTypeOptions() {
        return VehicleTypeOptions.VALUES;
    }

    @ModelAttribute("canEditVehicle")
    public boolean canEditVehicle() {
        return SecurityAuthHelper.isAdmin() || userRightsService.isPublicEditAllowed();
    }

    @ModelAttribute("canDeleteVehicle")
    public boolean canDeleteVehicle() {
        return SecurityAuthHelper.isAdmin() || userRightsService.isPublicDeleteAllowed();
    }

    @ModelAttribute("showVehicleActions")
    public boolean showVehicleActions() {
        return canEditVehicle() || canDeleteVehicle();
    }
}
