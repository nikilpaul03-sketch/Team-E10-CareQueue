package com.teame10.healthcare.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Team E10 - Smart Public Healthcare Appointment and Queue Management System
 * HomeController: Serves the public landing page and displays application metadata.
 */
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Value("${spring.application.name}")
    private String appName;

    @Value("${app.team:Team E10}")
    private String teamName;

    @Value("${app.version:1.0.0-SNAPSHOT}")
    private String appVersion;

    @Value("${server.port:8080}")
    private String serverPort;

    @GetMapping("/")
    public String home(Model model) {
        logger.debug("Serving Team E10 Healthcare landing page");

        model.addAttribute("appName", appName);
        model.addAttribute("teamName", teamName);
        model.addAttribute("appVersion", appVersion);
        model.addAttribute("serverPort", serverPort);
        model.addAttribute("currentPhase", "Phase 1: Project Setup & Baseline UI Architecture");
        model.addAttribute("serverTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy - HH:mm:ss")));

        // Sample OPD Department previews for Phase 1 presentation
        List<DepartmentPreview> departments = List.of(
            new DepartmentPreview("General Medicine", "Primary OPD consultations, routine health checkups, and triage.", "bi-heart-pulse", 12),
            new DepartmentPreview("Cardiology", "Heart health, ECG reviews, blood pressure management, and specialist care.", "bi-activity", 6),
            new DepartmentPreview("Pediatrics", "Infant and child wellness, immunizations, and specialized pediatric OPD.", "bi-person-hearts", 8),
            new DepartmentPreview("Orthopedics", "Bone, joint, fracture care, mobility assistance, and physiotherapy.", "bi-person-standing", 5),
            new DepartmentPreview("Ophthalmology", "Vision testing, eye examinations, cataract screening, and lens checks.", "bi-eye", 4),
            new DepartmentPreview("ENT (Ear, Nose & Throat)", "Auditory evaluations, sinus therapy, allergy management, and throat care.", "bi-headset", 5)
        );
        model.addAttribute("departments", departments);

        // Core system modules overview aligned with Document 1 and Document 2
        List<ModuleInfo> modules = List.of(
            new ModuleInfo("Patient Portal", "Self-service registration, doctor search, slot selection, and digital token tracking.", "bi-person-check"),
            new ModuleInfo("Doctor Schedule & Clinic Hours", "Specialty management, clinic sessions, slot capacities, and daily patient list.", "bi-calendar-check"),
            new ModuleInfo("Digital Token & Live Queue", "Automated token issuance, display counters, and estimated waiting time tracking.", "bi-ticket-perforated"),
            new ModuleInfo("Receptionist Desk", "Walk-in check-in, token progression, arrival verification, and no-show handling.", "bi-display"),
            new ModuleInfo("Administrative Governance", "Doctor/staff provisioning, clinic configuration, audit logging, and role security.", "bi-shield-check"),
            new ModuleInfo("Operational Reports", "Footfall analytics, cancellation trends, waiting time reports, and peak hour insights.", "bi-bar-chart-line")
        );
        model.addAttribute("modules", modules);

        return "index";
    }

    /**
     * DTO for Department display in Phase 1
     */
    public record DepartmentPreview(String name, String description, String icon, int doctorCount) {}

    /**
     * DTO for System Module description
     */
    public record ModuleInfo(String title, String description, String icon) {}
}
