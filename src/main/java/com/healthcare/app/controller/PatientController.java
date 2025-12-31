package com.healthcare.app.controller;

import com.healthcare.app.model.Patient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class PatientController {

    private List<Patient> patients = new ArrayList<>();
    private AtomicLong counter = new AtomicLong();

    public PatientController() {
        // Add some dummy data
        patients.add(new Patient(counter.incrementAndGet(), "John Doe", 45, "Hypertension"));
        patients.add(new Patient(counter.incrementAndGet(), "Jane Smith", 32, "Migraine"));
    }

    @GetMapping("/")
    public String home() {
        return "index"; // Now points to the new Home Page
    }

    @GetMapping("/patients")
    public String listPatients(Model model) {
        model.addAttribute("patients", patients);
        return "patients"; // Was index.html
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "register";
    }

    @PostMapping("/register")
    public String registerPatient(@ModelAttribute Patient patient) {
        patient.setId(counter.incrementAndGet());
        patients.add(patient);
        return "redirect:/patients";
    }

    @GetMapping("/recommendation")
    public String showRecommendationForm() {
        return "recommendation";
    }

    @PostMapping("/recommendation")
    public String getRecommendation(@RequestParam("symptoms") String symptoms, Model model) {
        String diagnosis = recommendDiagnosis(symptoms);
        model.addAttribute("symptoms", symptoms);
        model.addAttribute("diagnosis", diagnosis);
        return "recommendation";
    }

    private String recommendDiagnosis(String symptoms) {
        symptoms = symptoms.toLowerCase();
        if (symptoms.contains("headache"))
            return "Migraine - Rest and hydration recommended.";
        if (symptoms.contains("fever"))
            return "Viral Infection - Paracetamol and rest.";
        if (symptoms.contains("cough"))
            return "Upper Respiratory Infection - Syrup and warm fluids.";
        if (symptoms.contains("pain"))
            return "General Pain - Consult a specialist if persists.";
        return "General Checkup Required - Please visit a doctor.";
    }
}
