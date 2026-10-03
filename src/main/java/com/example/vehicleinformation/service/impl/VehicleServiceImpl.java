package com.example.vehicleinformation.service.impl;

import com.example.vehicleinformation.dto.DashboardStatusDto;
import com.example.vehicleinformation.dto.ImportResultDto;
import com.example.vehicleinformation.dto.VehicleFormDto;
import com.example.vehicleinformation.dto.VehiclePageDto;
import com.example.vehicleinformation.exception.DuplicateVehicleException;
import com.example.vehicleinformation.exception.ExcelFileException;
import com.example.vehicleinformation.exception.VehicleNotFoundException;
import com.example.vehicleinformation.model.Vehicle;
import com.example.vehicleinformation.repository.VehicleRepository;
import com.example.vehicleinformation.service.VehicleService;
import com.example.vehicleinformation.util.BlockOptions;
import com.example.vehicleinformation.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class VehicleServiceImpl implements VehicleService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(VehicleServiceImpl.class);

    private static final Set<String> ALLOWED_SORTS = Set.of("type", "number", "block", "house", "name");
    private static final Set<Integer> ALLOWED_SIZES = Set.of(10, 25, 50, 100);

    private final VehicleRepository repository;

    public VehicleServiceImpl(VehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public VehiclePageDto search(
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
    ) {
        int safeSize = ALLOWED_SIZES.contains(size) ? size : 10;
        int safePage = Math.max(page, 0);
        String safeSort = ALLOWED_SORTS.contains(sort) ? sort : "house";
        String safeDirection = "desc".equalsIgnoreCase(direction) ? "desc" : "asc";

        String trimmedType = trimToEmpty(vehicleType);
        String trimmedNumber = trimToEmpty(vehicleNumber).replaceAll("\\s+", "");
        String trimmedBlock = trimToEmpty(blockNo);
        String trimmedHouse = trimToEmpty(houseNo);
        String trimmedName = trimToEmpty(name);
        String trimmedMobile = trimToEmpty(mobileNumber);

        boolean incompleteRtoPrefix = isIncompleteRtoPrefix(trimmedNumber);
        String effectiveVehicleNumber = incompleteRtoPrefix ? "" : trimmedNumber;

        boolean hasOtherCriteria = StringUtils.hasText(trimmedType)
                || StringUtils.hasText(trimmedBlock)
                || StringUtils.hasText(trimmedHouse)
                || StringUtils.hasText(trimmedName)
                || StringUtils.hasText(trimmedMobile);
        boolean hasUsableVehicleNumber = StringUtils.hasText(effectiveVehicleNumber);
        boolean hasAnyUsableCriteria = hasUsableVehicleNumber || hasOtherCriteria;

        boolean listAll = listAllWhenEmpty && !hasAnyUsableCriteria && !incompleteRtoPrefix;
        if (!hasAnyUsableCriteria && !listAll) {
            String message = incompleteRtoPrefix
                    ? "Type more of the vehicle number after the prefix (for example after GJ01 / GJ05) to see results."
                    : "Enter search criteria to view vehicle details.";
            return emptyPage(safePage, safeSize, safeSort, safeDirection,
                    trimmedType, trimmedNumber, trimmedBlock, trimmedHouse, trimmedName, trimmedMobile,
                    false, message);
        }

        List<Vehicle> filtered = repository.findByStatus(Vehicle.STATUS_ACTIVE).stream()
                .filter(v -> matches(v.getVehicleType(), trimmedType))
                .filter(v -> matches(v.getVehicleNumber(), effectiveVehicleNumber))
                .filter(v -> matches(v.getBlockNo(), trimmedBlock))
                .filter(v -> matches(v.getHouseNo(), trimmedHouse))
                .filter(v -> matches(v.getName(), trimmedName))
                .filter(v -> matches(v.getMobileNumber(), trimmedMobile))
                .sorted(buildComparator(safeSort, safeDirection))
                .toList();

        int totalElements = filtered.size();
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / safeSize);
        if (totalPages > 0 && safePage >= totalPages) {
            safePage = totalPages - 1;
        }
        int from = Math.min(safePage * safeSize, totalElements);
        int to = Math.min(from + safeSize, totalElements);
        List<Vehicle> pageContent = from >= to ? List.of() : filtered.subList(from, to);

        String message = totalElements == 0 ? "No vehicle records found." : null;

        return VehiclePageDto.builder()
                .content(pageContent)
                .page(safePage)
                .size(safeSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .sort(safeSort)
                .direction(safeDirection)
                .vehicleType(trimmedType)
                .vehicleNumber(trimmedNumber)
                .blockNo(trimmedBlock)
                .houseNo(trimmedHouse)
                .name(trimmedName)
                .mobileNumber(trimmedMobile)
                .searched(true)
                .searchMessage(message)
                .build();
    }

    private VehiclePageDto emptyPage(
            int page,
            int size,
            String sort,
            String direction,
            String vehicleType,
            String vehicleNumber,
            String blockNo,
            String houseNo,
            String name,
            String mobileNumber,
            boolean searched,
            String message
    ) {
        return VehiclePageDto.builder()
                .content(List.of())
                .page(page)
                .size(size)
                .totalElements(0)
                .totalPages(0)
                .sort(sort)
                .direction(direction)
                .vehicleType(vehicleType)
                .vehicleNumber(vehicleNumber)
                .blockNo(blockNo)
                .houseNo(houseNo)
                .name(name)
                .mobileNumber(mobileNumber)
                .searched(searched)
                .searchMessage(message)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehicle> findById(Long id) {
        return repository.findByIdAndStatus(id, Vehicle.STATUS_ACTIVE);
    }

    @Override
    @Transactional
    public Vehicle create(VehicleFormDto form) {
        String normalizedNumber = ExcelUtil.normalizeVehicleNumber(form.getVehicleNumber());
        if (repository.existsByVehicleNumberIgnoreCase(normalizedNumber)) {
            throw new DuplicateVehicleException(normalizedNumber);
        }
        Vehicle vehicle = toEntity(form);
        vehicle.setVehicleNumber(normalizedNumber);
        vehicle.setStatus(Vehicle.STATUS_ACTIVE);
        Vehicle saved = repository.save(vehicle);
        log.info("Vehicle added successfully: {}", saved.getVehicleNumber());
        return saved;
    }

    @Override
    @Transactional
    public Vehicle update(Long id, VehicleFormDto form) {
        Vehicle existing = repository.findByIdAndStatus(id, Vehicle.STATUS_ACTIVE)
                .orElseThrow(() -> new VehicleNotFoundException(id));
        String normalizedNumber = ExcelUtil.normalizeVehicleNumber(form.getVehicleNumber());
        repository.findByVehicleNumberIgnoreCase(normalizedNumber).ifPresent(v -> {
            if (!v.getId().equals(id)) {
                throw new DuplicateVehicleException(normalizedNumber);
            }
        });
        existing.setVehicleType(form.getVehicleType().trim());
        existing.setVehicleNumber(normalizedNumber);
        existing.setBlockNo(form.getBlockNo().trim().toUpperCase(Locale.ROOT));
        existing.setHouseNo(form.getHouseNo().trim());
        existing.setName(form.getName().trim());
        existing.setMobileNumber(ExcelUtil.normalizeMobile(form.getMobileNumber()));
        existing.setStatus(Vehicle.STATUS_ACTIVE);
        Vehicle saved = repository.save(existing);
        log.info("Vehicle updated successfully: id={}", id);
        return saved;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Vehicle existing = repository.findByIdAndStatus(id, Vehicle.STATUS_ACTIVE)
                .orElseThrow(() -> new VehicleNotFoundException(id));
        existing.setStatus(Vehicle.STATUS_DELETED);
        repository.save(existing);
        log.info("Vehicle soft-deleted (status=0): id={}", id);
    }

    @Override
    @Transactional
    public void rollback(Long id) {
        Vehicle existing = repository.findByIdAndStatus(id, Vehicle.STATUS_DELETED)
                .orElseThrow(() -> new VehicleNotFoundException(id));
        existing.setStatus(Vehicle.STATUS_ACTIVE);
        repository.save(existing);
        log.info("Vehicle restored (status=1): id={}", id);
    }

    @Override
    @Transactional
    public void permanentDelete(Long id) {
        Vehicle existing = repository.findByIdAndStatus(id, Vehicle.STATUS_DELETED)
                .orElseThrow(() -> new VehicleNotFoundException(id));
        repository.delete(existing);
        log.info("Vehicle permanently deleted: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> listDeleted() {
        List<Vehicle> deleted = repository.findByStatus(Vehicle.STATUS_DELETED);
        deleted.sort(Comparator
                .comparing((Vehicle v) -> safeString(v.getBlockNo()), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(v -> safeString(v.getHouseNo()), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(v -> safeString(v.getVehicleNumber()), String.CASE_INSENSITIVE_ORDER));
        return deleted;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatusDto getDashboardStats() {
        List<Vehicle> active = repository.findByStatus(Vehicle.STATUS_ACTIVE);
        long cars = active.stream().filter(v -> containsType(v.getVehicleType(), "car")).count();
        long bikes = active.stream().filter(v -> {
            String t = v.getVehicleType() == null ? "" : v.getVehicleType().toLowerCase(Locale.ROOT);
            return t.contains("bike") || t.contains("scooter") || t.contains("motorcycle") || t.contains("two");
        }).count();
        return DashboardStatusDto.builder()
                .totalVehicles(active.size())
                .totalCars(cars)
                .totalBikes(bikes)
                .totalActiveRecords(active.size())
                .build();
    }

    @Override
    @Transactional
    public ImportResultDto importExcel(MultipartFile file, String mode, boolean confirmed) {
        log.info("Excel import started. mode={}", mode);
        validateUpload(file);

        boolean replace = "replace".equalsIgnoreCase(mode);
        if (replace && !confirmed) {
            throw new ExcelFileException("Replace mode requires confirmation. Please confirm before overwriting existing data.");
        }

        List<Vehicle> importedVehicles;
        try (InputStream in = file.getInputStream();
             Workbook workbook = ExcelUtil.openWorkbook(in)) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                throw new ExcelFileException("Import file has no sheets");
            }
            importedVehicles = ExcelUtil.readImportVehicles(sheet);
        } catch (ExcelFileException e) {
            throw e;
        } catch (IOException e) {
            throw new ExcelFileException("Failed to read import file", e);
        }

        String modeLabel = replace ? "Replace Existing Data" : "Append Data";
        ImportResultDto result = ImportResultDto.builder()
                .totalRows(importedVehicles.size())
                .mode(modeLabel)
                .build();

        List<Vehicle> valid = new ArrayList<>();
        Set<String> seenNumbers = new HashSet<>();
        Set<String> existingNumbers = new HashSet<>();
        if (!replace) {
            repository.findAll().forEach(v ->
                    existingNumbers.add(ExcelUtil.normalizeVehicleNumber(v.getVehicleNumber())));
        }

        int rowNum = 1;
        for (Vehicle vehicle : importedVehicles) {
            rowNum++;
            List<String> rowErrors = validateImportRow(vehicle, rowNum);
            if (!rowErrors.isEmpty()) {
                result.getErrors().addAll(rowErrors);
                result.setInvalidRows(result.getInvalidRows() + 1);
                continue;
            }
            String normalized = ExcelUtil.normalizeVehicleNumber(vehicle.getVehicleNumber());
            vehicle.setId(null);
            vehicle.setVehicleNumber(normalized);
            vehicle.setMobileNumber(ExcelUtil.normalizeMobile(vehicle.getMobileNumber()));
            vehicle.setStatus(Vehicle.STATUS_ACTIVE);
            if (seenNumbers.contains(normalized) || existingNumbers.contains(normalized)) {
                result.getErrors().add("Row " + rowNum + ": Duplicate vehicle number: " + normalized);
                result.setSkippedDuplicates(result.getSkippedDuplicates() + 1);
                continue;
            }
            seenNumbers.add(normalized);
            valid.add(vehicle);
        }

        if (replace) {
            repository.deleteAllInBatch();
            if (!valid.isEmpty()) {
                repository.saveAll(valid);
            }
        } else if (!valid.isEmpty()) {
            repository.saveAll(valid);
        }

        result.setImported(valid.size());
        log.info("Excel import completed. {}", result.toUserMessage().replace('\n', ' '));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportExcel() {
        List<Vehicle> vehicles = repository.findByStatus(Vehicle.STATUS_ACTIVE);
        vehicles.sort(Comparator
                .comparing((Vehicle v) -> safeString(v.getBlockNo()), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(v -> safeString(v.getHouseNo()), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(v -> safeString(v.getVehicleNumber()), String.CASE_INSENSITIVE_ORDER));
        log.info("Excel export started. records={}", vehicles.size());
        return ExcelUtil.createExportWorkbookBytes(vehicles);
    }

    @Override
    public byte[] createDemoExcel() {
        log.info("Demo Excel template requested");
        return ExcelUtil.createDemoWorkbookBytes();
    }

    private void validateUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ExcelFileException("Please select an Excel (.xlsx) file to upload.");
        }
        String original = file.getOriginalFilename();
        if (original == null) {
            throw new ExcelFileException("Invalid file name.");
        }
        String sanitized = original.replace('\\', '/');
        if (sanitized.contains("..") || sanitized.contains("/")) {
            throw new ExcelFileException("Invalid file name.");
        }
        if (!original.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            throw new ExcelFileException("Only .xlsx Excel files are allowed.");
        }
        String contentType = file.getContentType();
        if (contentType != null
                && !contentType.isBlank()
                && !contentType.contains("spreadsheet")
                && !contentType.contains("excel")
                && !contentType.equals("application/octet-stream")
                && !contentType.equals("application/zip")) {
            throw new ExcelFileException("Invalid file type. Please upload a valid .xlsx file.");
        }
    }

    private List<String> validateImportRow(Vehicle vehicle, int rowNum) {
        List<String> errors = new ArrayList<>();
        if (isBlank(vehicle.getVehicleType())) {
            errors.add("Row " + rowNum + ": Vehicle type is required");
        }
        if (isBlank(vehicle.getVehicleNumber())) {
            errors.add("Row " + rowNum + ": Vehicle number is required");
        }
        if (isBlank(vehicle.getBlockNo())) {
            errors.add("Row " + rowNum + ": Block number is required");
        } else if (!BlockOptions.isValid(vehicle.getBlockNo())) {
            errors.add("Row " + rowNum + ": Block number must be A, B, C, D, E, F, or G");
        } else {
            vehicle.setBlockNo(vehicle.getBlockNo().trim().toUpperCase(Locale.ROOT));
        }
        if (isBlank(vehicle.getHouseNo())) {
            errors.add("Row " + rowNum + ": House number is required");
        }
        if (isBlank(vehicle.getName())) {
            errors.add("Row " + rowNum + ": Name is required");
        }
        String mobile = ExcelUtil.normalizeMobile(vehicle.getMobileNumber());
        if (!mobile.matches("\\d{10}")) {
            errors.add("Row " + rowNum + ": Mobile number must be exactly 10 digits");
        }
        return errors;
    }

    private boolean matches(String value, String criterion) {
        if (!StringUtils.hasText(criterion)) {
            return true;
        }
        if (value == null) {
            return false;
        }
        return value.toLowerCase(Locale.ROOT).contains(criterion.trim().toLowerCase(Locale.ROOT));
    }

    private boolean containsType(String type, String keyword) {
        return type != null && type.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private Comparator<Vehicle> buildComparator(String sort, String direction) {
        Comparator<Vehicle> comparator = switch (sort) {
            case "number" -> Comparator.comparing(v -> safeString(v.getVehicleNumber()), String.CASE_INSENSITIVE_ORDER);
            case "block" -> Comparator.comparing(v -> safeString(v.getBlockNo()), String.CASE_INSENSITIVE_ORDER);
            case "house" -> Comparator.comparing(v -> safeString(v.getHouseNo()), String.CASE_INSENSITIVE_ORDER);
            case "name" -> Comparator.comparing(v -> safeString(v.getName()), String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(v -> safeString(v.getVehicleType()), String.CASE_INSENSITIVE_ORDER);
        };
        return "desc".equals(direction) ? comparator.reversed() : comparator;
    }

    private String safeString(String value) {
        return value != null ? value : "";
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isIncompleteRtoPrefix(String vehicleNumber) {
        if (!StringUtils.hasText(vehicleNumber)) {
            return false;
        }
        String n = vehicleNumber.trim().replaceAll("\\s+", "");
        return n.length() <= 4 && n.matches("(?i)^[A-Z]{2}\\d{0,2}$");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Vehicle toEntity(VehicleFormDto form) {
        return Vehicle.builder()
                .vehicleType(form.getVehicleType().trim())
                .vehicleNumber(form.getVehicleNumber().trim())
                .blockNo(form.getBlockNo().trim().toUpperCase(Locale.ROOT))
                .houseNo(form.getHouseNo().trim())
                .name(form.getName().trim())
                .mobileNumber(ExcelUtil.normalizeMobile(form.getMobileNumber()))
                .status(Vehicle.STATUS_ACTIVE)
                .build();
    }
}
