package com.example.vehicleinformation.config;

import com.example.vehicleinformation.service.UserRightsService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserRightsInitializer {

    private final UserRightsService userRightsService;

    public UserRightsInitializer(UserRightsService userRightsService) {
        this.userRightsService = userRightsService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        userRightsService.ensureDefaults();
    }
}
