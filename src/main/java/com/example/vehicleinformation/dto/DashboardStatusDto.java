package com.example.vehicleinformation.dto;

public class DashboardStatusDto {

    private long totalVehicles;
    private long totalCars;
    private long totalBikes;
    private long totalActiva;
    private long totalOthers;

    public DashboardStatusDto() {
    }

    public DashboardStatusDto(long totalVehicles, long totalCars, long totalBikes,
                              long totalActiva, long totalOthers) {
        this.totalVehicles = totalVehicles;
        this.totalCars = totalCars;
        this.totalBikes = totalBikes;
        this.totalActiva = totalActiva;
        this.totalOthers = totalOthers;
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

    public long getTotalActiva() {
        return totalActiva;
    }

    public void setTotalActiva(long totalActiva) {
        this.totalActiva = totalActiva;
    }

    public long getTotalOthers() {
        return totalOthers;
    }

    public void setTotalOthers(long totalOthers) {
        this.totalOthers = totalOthers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private long totalVehicles;
        private long totalCars;
        private long totalBikes;
        private long totalActiva;
        private long totalOthers;

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

        public Builder totalActiva(long totalActiva) {
            this.totalActiva = totalActiva;
            return this;
        }

        public Builder totalOthers(long totalOthers) {
            this.totalOthers = totalOthers;
            return this;
        }

        public DashboardStatusDto build() {
            return new DashboardStatusDto(totalVehicles, totalCars, totalBikes, totalActiva, totalOthers);
        }
    }
}
