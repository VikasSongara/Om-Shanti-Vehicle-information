package com.example.vehicleinformation.controller;

import com.example.vehicleinformation.scheduler.SchedulerRuntimeState;
import com.example.vehicleinformation.scheduler.VehicleWarmupScheduler;
import com.example.vehicleinformation.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final DateTimeFormatter LAST_RUN_FORMAT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss").withZone(ZoneId.systemDefault());

    private final VehicleService vehicleService;
    private final VehicleWarmupScheduler warmupScheduler;

    public AdminController(VehicleService vehicleService, VehicleWarmupScheduler warmupScheduler) {
        this.vehicleService = vehicleService;
        this.warmupScheduler = warmupScheduler;
    }

    @GetMapping({"/dashboard", "", "/"})
    public String dashboard(Model model) {
        model.addAttribute("stats", vehicleService.getDashboardStats());
        return "admin-dashboard";
    }

    @GetMapping("/scheduler")
    public String schedulerPage(Model model) {
        SchedulerRuntimeState state = warmupScheduler.getState();
        model.addAttribute("intervalMinutes", state.getIntervalMinutes());
        model.addAttribute("enabled", state.isEnabled());
        model.addAttribute("lastRunSummary", state.getLastRunSummary());
        model.addAttribute("lastError", state.getLastError());
        model.addAttribute("lastRunAt",
                state.getLastRunAt() == null ? "—" : LAST_RUN_FORMAT.format(state.getLastRunAt()));
        return "scheduler";
    }

    @PostMapping("/scheduler")
    public String updateScheduler(@RequestParam int intervalMinutes,
                                  @RequestParam(defaultValue = "false") boolean enabled,
                                  RedirectAttributes redirectAttributes) {
        try {
            warmupScheduler.updateSettings(intervalMinutes, enabled);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Scheduler updated: every " + intervalMinutes + " minute(s), "
                            + (enabled ? "enabled" : "disabled") + ".");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/scheduler";
    }

    @PostMapping("/scheduler/run-now")
    public String runSchedulerNow(RedirectAttributes redirectAttributes) {
        warmupScheduler.runNow();
        redirectAttributes.addFlashAttribute("successMessage", "Scheduler ran once now.");
        return "redirect:/admin/scheduler";
    }
}
