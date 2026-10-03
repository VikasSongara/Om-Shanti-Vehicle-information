package com.example.vehicleinformation.dto;

import java.util.ArrayList;
import java.util.List;

public class ImportResultDto {

    private int totalRows;
    private int imported;
    private int skippedDuplicates;
    private int invalidRows;
    private String mode;
    private List<String> errors = new ArrayList<>();

    public ImportResultDto() {
    }

    public ImportResultDto(int totalRows, int imported, int skippedDuplicates, int invalidRows, String mode, List<String> errors) {
        this.totalRows = totalRows;
        this.imported = imported;
        this.skippedDuplicates = skippedDuplicates;
        this.invalidRows = invalidRows;
        this.mode = mode;
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getImported() {
        return imported;
    }

    public void setImported(int imported) {
        this.imported = imported;
    }

    public int getSkippedDuplicates() {
        return skippedDuplicates;
    }

    public void setSkippedDuplicates(int skippedDuplicates) {
        this.skippedDuplicates = skippedDuplicates;
    }

    public int getInvalidRows() {
        return invalidRows;
    }

    public void setInvalidRows(int invalidRows) {
        this.invalidRows = invalidRows;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String toUserMessage() {
        return String.format(
                "Import completed. Total rows: %d, Imported: %d, Skipped duplicates: %d, Invalid rows: %d",
                totalRows, imported, skippedDuplicates, invalidRows);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private int totalRows;
        private int imported;
        private int skippedDuplicates;
        private int invalidRows;
        private String mode;
        private List<String> errors = new ArrayList<>();

        public Builder totalRows(int totalRows) {
            this.totalRows = totalRows;
            return this;
        }

        public Builder imported(int imported) {
            this.imported = imported;
            return this;
        }

        public Builder skippedDuplicates(int skippedDuplicates) {
            this.skippedDuplicates = skippedDuplicates;
            return this;
        }

        public Builder invalidRows(int invalidRows) {
            this.invalidRows = invalidRows;
            return this;
        }

        public Builder mode(String mode) {
            this.mode = mode;
            return this;
        }

        public Builder errors(List<String> errors) {
            this.errors = errors != null ? errors : new ArrayList<>();
            return this;
        }

        public ImportResultDto build() {
            return new ImportResultDto(totalRows, imported, skippedDuplicates, invalidRows, mode, errors);
        }
    }
}
