package com.example.vehicleinformation.service;

import com.example.vehicleinformation.dto.DashboardStatusDto;
import com.example.vehicleinformation.dto.ImportResultDto;
import com.example.vehicleinformation.dto.VehicleFormDto;
import com.example.vehicleinformation.dto.VehiclePageDto;
import com.example.vehicleinformation.model.Vehicle;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface VehicleService {

    VehiclePageDto search(
            String vehicleType,
            String vehicleNumber,
            String blockNo,
            String houseNo,
            String name,
            String mobileNumber,
            int page,
            int size,
            String sort,
            String direction,
            boolean listAllWhenEmpty
    );

    Optional<Vehicle> findById(Long id);

    Vehicle create(VehicleFormDto form);

    Vehicle update(Long id, VehicleFormDto form);

    /** Soft-delete: sets status = 0. */
    void delete(Long id);

    /** Restore soft-deleted vehicle: sets status = 1. */
    void rollback(Long id);

    /** Permanently remove a soft-deleted vehicle. */
    void permanentDelete(Long id);

    List<Vehicle> listDeleted();

    DashboardStatusDto getDashboardStats();

    ImportResultDto importExcel(MultipartFile file, String mode, boolean confirmed);

    byte[] exportExcel();

    byte[] createDemoExcel();
}
