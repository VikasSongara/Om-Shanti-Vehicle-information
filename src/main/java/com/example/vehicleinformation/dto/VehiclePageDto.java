package com.example.vehicleinformation.dto;

import com.example.vehicleinformation.model.Vehicle;

import java.util.Collections;
import java.util.List;

public class VehiclePageDto {

    private List<Vehicle> content = Collections.emptyList();
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private String sort;
    private String direction;

    private String vehicleType;
    private String vehicleNumber;
    private String blockNo;
    private String houseNo;
    private String name;
    private String mobileNumber;

    /** True when the user has submitted a search with enough criteria to load data. */
    private boolean searched;
    private String searchMessage;

    public VehiclePageDto() {
    }

    public VehiclePageDto(List<Vehicle> content, int page, int size, long totalElements, int totalPages,
                          String sort, String direction, String vehicleType, String vehicleNumber,
                          String blockNo, String houseNo, String name, String mobileNumber,
                          boolean searched, String searchMessage) {
        this.content = content != null ? content : Collections.emptyList();
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.sort = sort;
        this.direction = direction;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.blockNo = blockNo;
        this.houseNo = houseNo;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.searched = searched;
        this.searchMessage = searchMessage;
    }

    public List<Vehicle> getContent() {
        return content;
    }

    public void setContent(List<Vehicle> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
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

    public boolean isSearched() {
        return searched;
    }

    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    public String getSearchMessage() {
        return searchMessage;
    }

    public void setSearchMessage(String searchMessage) {
        this.searchMessage = searchMessage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<Vehicle> content = Collections.emptyList();
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private String sort;
        private String direction;
        private String vehicleType;
        private String vehicleNumber;
        private String blockNo;
        private String houseNo;
        private String name;
        private String mobileNumber;
        private boolean searched;
        private String searchMessage;

        public Builder content(List<Vehicle> content) {
            this.content = content;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder totalElements(long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public Builder totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder sort(String sort) {
            this.sort = sort;
            return this;
        }

        public Builder direction(String direction) {
            this.direction = direction;
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

        public Builder searched(boolean searched) {
            this.searched = searched;
            return this;
        }

        public Builder searchMessage(String searchMessage) {
            this.searchMessage = searchMessage;
            return this;
        }

        public VehiclePageDto build() {
            return new VehiclePageDto(content, page, size, totalElements, totalPages, sort, direction,
                    vehicleType, vehicleNumber, blockNo, houseNo, name, mobileNumber, searched, searchMessage);
        }
    }
}
