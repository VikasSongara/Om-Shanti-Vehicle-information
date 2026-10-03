package com.example.vehicleinformation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "vehicles",
        uniqueConstraints = @UniqueConstraint(name = "uk_vehicles_vehicle_number", columnNames = "vehicle_number")
)
public class Vehicle {

    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_DELETED = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_type", nullable = false, length = 50)
    private String vehicleType;

    @Column(name = "vehicle_number", nullable = false, length = 20)
    private String vehicleNumber;

    @Column(name = "block_no", nullable = false, length = 10)
    private String blockNo;

    @Column(name = "house_no", nullable = false, length = 20)
    private String houseNo;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "mobile_number", nullable = false, length = 10)
    private String mobileNumber;

    /**
     * 1 = active (shown in Vehicles), 0 = soft-deleted (admin Deleted list).
     * Default 1 is required so SQL Server can add the column to a non-empty table.
     */
    @Column(name = "status", columnDefinition = "int default 1")
    private Integer status = STATUS_ACTIVE;

    public Vehicle() {
    }

    public Vehicle(Long id, String vehicleType, String vehicleNumber, String blockNo, String houseNo,
                   String name, String mobileNumber, Integer status) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.blockNo = blockNo;
        this.houseNo = houseNo;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.status = status == null ? STATUS_ACTIVE : status;
    }

    @PrePersist
    void applyDefaultStatus() {
        if (status == null) {
            status = STATUS_ACTIVE;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getBlockNo() {
        return blockNo;
    }

    public void setBlockNo(String blockNo) {
        this.blockNo = blockNo;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean isActive() {
        // Treat null as active for rows created before the column was backfilled.
        return status == null || status == STATUS_ACTIVE;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private String vehicleType;
        private String vehicleNumber;
        private String blockNo;
        private String houseNo;
        private String name;
        private String mobileNumber;
        private Integer status = STATUS_ACTIVE;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder vehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
            return this;
        }

        public Builder vehicleNumber(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
            return this;
        }

        public Builder blockNo(String blockNo) {
            this.blockNo = blockNo;
            return this;
        }

        public Builder houseNo(String houseNo) {
            this.houseNo = houseNo;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder mobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
            return this;
        }

        public Builder status(Integer status) {
            this.status = status;
            return this;
        }

        public Vehicle build() {
            return new Vehicle(id, vehicleType, vehicleNumber, blockNo, houseNo, name, mobileNumber, status);
        }
    }
}
