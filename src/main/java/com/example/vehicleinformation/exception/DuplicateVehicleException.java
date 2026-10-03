package com.example.vehicleinformation.exception;

public class DuplicateVehicleException extends RuntimeException {

    public DuplicateVehicleException(String vehicleNumber) {
        super("Vehicle number already exists.");
    }
}
