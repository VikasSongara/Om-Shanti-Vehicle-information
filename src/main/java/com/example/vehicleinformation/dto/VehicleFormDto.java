package com.example.vehicleinformation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class VehicleFormDto {

    private Long id;

    @NotBlank(message = "Vehicle type is required")
    @Size(max = 50, message = "Vehicle type must be at most 50 characters")
    private String vehicleType;

    @NotBlank(message = "Vehicle number is required")
    @Size(max = 20, message = "Vehicle number must be at most 20 characters")
    private String vehicleNumber;

    @NotBlank(message = "Block number is required")
    @Pattern(regexp = "^[A-G]$", message = "Block number must be A, B, C, D, E, F, or G")
    private String blockNo;

    @NotBlank(message = "House number is required")
    @Size(max = 20, message = "House number must be at most 20 characters")
    private String houseNo;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobileNumber;

    public VehicleFormDto() {
    }

    public VehicleFormDto(Long id, String vehicleType, String vehicleNumber, String blockNo, String houseNo, String name, String mobileNumber) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.blockNo = blockNo;
        this.houseNo = houseNo;
        this.name = name;
        this.mobileNumber = mobileNumber;
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

        public VehicleFormDto build() {
            return new VehicleFormDto(id, vehicleType, vehicleNumber, blockNo, houseNo, name, mobileNumber);
        }
    }
}
