package com.example.vehicleinformation.controller;

import com.example.vehicleinformation.dto.ImportResultDto;
import com.example.vehicleinformation.dto.VehicleFormDto;
import com.example.vehicleinformation.dto.VehiclePageDto;
import com.example.vehicleinformation.exception.DuplicateVehicleException;
import com.example.vehicleinformation.exception.VehicleNotFoundException;
import com.example.vehicleinformation.model.Vehicle;
import com.example.vehicleinformation.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping({"", "/search"})
    public String list(@RequestParam(required = false) String vehicleType,
                       @RequestParam(required = false) String vehicleNumber,
                       @RequestParam(required = false) String blockNo,
                       @RequestParam(required = false) String houseNo,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String mobileNumber,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "house") String sort,
                       @RequestParam(defaultValue = "asc") String direction,
                       Model model) {
        boolean admin = isAdmin();
        if (!admin) {
            vehicleType = null;
            blockNo = null;
            name = null;
            mobileNumber = null;
        }
        VehiclePageDto pageDto = vehicleService.search(
                vehicleType, vehicleNumber, blockNo, houseNo, name, mobileNumber,
                page, size, sort, direction, false
        );
        model.addAttribute("vehiclePage", pageDto);
        return "vehicles";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("vehicleForm", new VehicleFormDto());
        model.addAttribute("formTitle", "Add Vehicle");
        model.addAttribute("formAction", "/vehicles/add");
        return "vehicle-form";
    }

    @PostMapping("/add")
    public String addSubmit(@Valid @ModelAttribute("vehicleForm") VehicleFormDto form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "Add Vehicle");
            model.addAttribute("formAction", "/vehicles/add");
            return "vehicle-form";
        }
        try {
            vehicleService.create(form);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle added successfully.");
            return "redirect:/vehicles";
        } catch (DuplicateVehicleException ex) {
            bindingResult.rejectValue("vehicleNumber", "duplicate", "Vehicle number already exists.");
            model.addAttribute("formTitle", "Add Vehicle");
            model.addAttribute("formAction", "/vehicles/add");
            return "vehicle-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));
        VehicleFormDto form = VehicleFormDto.builder()
                .id(vehicle.getId())
                .vehicleType(vehicle.getVehicleType())
                .vehicleNumber(vehicle.getVehicleNumber())
                .blockNo(vehicle.getBlockNo())
                .houseNo(vehicle.getHouseNo())
                .name(vehicle.getName())
                .mobileNumber(vehicle.getMobileNumber())
                .build();
        model.addAttribute("vehicleForm", form);
        model.addAttribute("formTitle", "Edit Vehicle");
        model.addAttribute("formAction", "/vehicles/edit/" + id);
        return "vehicle-form";
    }

    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable Long id,
                             @Valid @ModelAttribute("vehicleForm") VehicleFormDto form,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "Edit Vehicle");
            model.addAttribute("formAction", "/vehicles/edit/" + id);
            return "vehicle-form";
        }
        try {
            vehicleService.update(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle updated successfully.");
            return "redirect:/vehicles";
        } catch (DuplicateVehicleException ex) {
            bindingResult.rejectValue("vehicleNumber", "duplicate", "Vehicle number already exists.");
            model.addAttribute("formTitle", "Edit Vehicle");
            model.addAttribute("formAction", "/vehicles/edit/" + id);
            return "vehicle-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vehicleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Vehicle moved to Deleted. You can restore it later.");
        return "redirect:/vehicles";
    }

    @GetMapping("/deleted")
    public String deletedList(Model model) {
        model.addAttribute("deletedVehicles", vehicleService.listDeleted());
        return "deleted-vehicles";
    }

    @PostMapping("/rollback/{id}")
    public String rollback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vehicleService.rollback(id);
        redirectAttributes.addFlashAttribute("successMessage", "Vehicle restored successfully.");
        return "redirect:/vehicles/deleted";
    }

    @PostMapping("/permanent-delete/{id}")
    public String permanentDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vehicleService.permanentDelete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Vehicle permanently deleted.");
        return "redirect:/vehicles/deleted";
    }

    @GetMapping("/import")
    public String importForm() {
        return "import-excel";
    }

    @PostMapping("/import")
    public String importSubmit(@RequestParam("file") MultipartFile file,
                               @RequestParam(defaultValue = "append") String mode,
                               @RequestParam(required = false) boolean confirmReplace,
                               RedirectAttributes redirectAttributes) {
        ImportResultDto result = vehicleService.importExcel(file, mode, confirmReplace);
        redirectAttributes.addFlashAttribute("importResult", result);
        redirectAttributes.addFlashAttribute("successMessage", result.toUserMessage());
        return "redirect:/vehicles/import";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel() {
        byte[] bytes = vehicleService.exportExcel();
        String filename = "vehicles-export-"
                + LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                + ".xlsx";
        return excelFileResponse(bytes, filename);
    }

    @GetMapping("/import/demo")
    public ResponseEntity<byte[]> downloadDemoExcel() {
        byte[] bytes = vehicleService.createDemoExcel();
        return excelFileResponse(bytes, "vehicle-import-demo.xlsx");
    }

    private ResponseEntity<byte[]> excelFileResponse(byte[] bytes, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(bytes.length)
                .body(bytes);
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
