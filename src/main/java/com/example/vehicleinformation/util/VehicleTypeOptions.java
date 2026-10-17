package com.example.vehicleinformation.util;

import java.util.List;

public final class VehicleTypeOptions {

    public static final List<String> VALUES = List.of(
            "Bike", "Activa", "Car", "Rickshaw", "Tempo"
    );

    public static final String PATTERN = "^(Bike|Activa|Car|Rickshaw|Tempo)$";

    private VehicleTypeOptions() {
    }

    public static boolean isValid(String vehicleType) {
        return vehicleType != null && VALUES.contains(vehicleType.trim());
    }
}
