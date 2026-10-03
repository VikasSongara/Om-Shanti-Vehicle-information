package com.example.vehicleinformation.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(VehicleNotFoundException.class)
    public String handleNotFound(VehicleNotFoundException ex, RedirectAttributes redirectAttributes) {
        log.warn("Vehicle not found: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", "Vehicle not found.");
        return "redirect:/vehicles";
    }

    @ExceptionHandler(DuplicateVehicleException.class)
    public String handleDuplicate(DuplicateVehicleException ex, RedirectAttributes redirectAttributes) {
        log.warn("Duplicate vehicle: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", "Vehicle number already exists.");
        return "redirect:/vehicles";
    }

    @ExceptionHandler(ExcelFileException.class)
    public String handleExcel(ExcelFileException ex, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        log.error("Excel error: {}", ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        String uri = request.getRequestURI();
        if (uri != null && uri.contains("/vehicles/import")) {
            return "redirect:/vehicles/import";
        }
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/import")) {
            return "redirect:/vehicles/import";
        }
        return "redirect:/vehicles";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUpload(MaxUploadSizeExceededException ex, RedirectAttributes redirectAttributes) {
        log.warn("Upload too large: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", "Uploaded file is too large. Maximum size is 5MB.");
        return "redirect:/vehicles/import";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResource(NoResourceFoundException ex) {
        log.debug("Static resource not found: {}", ex.getResourcePath());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneral(Exception ex, Model model) {
        log.error("Unexpected error", ex);
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
        return "error";
    }
}
