package com.example.vehicleinformation.repository;

import com.example.vehicleinformation.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByStatus(Integer status);

    Optional<Vehicle> findByIdAndStatus(Long id, Integer status);

    Optional<Vehicle> findByVehicleNumberIgnoreCase(String vehicleNumber);

    boolean existsByVehicleNumberIgnoreCase(String vehicleNumber);

    long countByStatus(Integer status);
}
