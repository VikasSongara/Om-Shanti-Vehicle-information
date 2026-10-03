package com.example.vehicleinformation.dto;

public class DashboardStatusDto {

    private long totalVehicles;
    private long totalCars;
    private long totalBikes;
    private long totalActiveRecords;

    public DashboardStatusDto() {
    }

    public DashboardStatusDto(long totalVehicles, long totalCars, long totalBikes, long totalActiveRecords) {
        this.totalVehicles = totalVehicles;
        this.totalCars = totalCars;
        this.totalBikes = totalBikes;
        this.totalActiveRecords = totalActiveRecords;
    }

    public long getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(long totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public long getTotalCars() {
        return totalCars;
    }

    public void setTotalCars(long totalCars) {
        this.totalCars = totalCars;
    }

    public long getTotalBikes() {
        return totalBikes;
    }

    public void setTotalBikes(long totalBikes) {
        this.totalBikes = totalBikes;
    }

    public long getTotalActiveRecords() {
        return totalActiveRecords;
    }

    public void setTotalActiveRecords(long totalActiveRecords) {
        this.totalActiveRecords = totalActiveRecords;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private long totalVehicles;
        private long totalCars;
        private long totalBikes;
        private long totalActiveRecords;

        public Builder totalVehicles(long totalVehicles) {
            this.totalVehicles = totalVehicles;
            return this;
        }

        public Builder totalCars(long totalCars) {
            this.totalCars = totalCars;
            return this;
        }

        public Builder totalBikes(long totalBikes) {
            this.totalBikes = totalBikes;
            return this;
        }

        public Builder totalActiveRecords(long totalActiveRecords) {
            this.totalActiveRecords = totalActiveRecords;
            return this;
        }

        public DashboardStatusDto build() {
            return new DashboardStatusDto(totalVehicles, totalCars, totalBikes, totalActiveRecords);
        }
    }
}
