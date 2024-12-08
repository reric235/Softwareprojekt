package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Student;
import com.example.StudiDocs.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PageController {

    private final StudentService studentService;

    @Autowired
    public PageController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/index")
    public String index(Model model, Authentication authentication) {
        boolean isAdmin = false;
        boolean isStudentenrat = false;

        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String role = authority.getAuthority();
                if ("ROLE_ADMIN".equals(role)) {
                    isAdmin = true;
                }
                if ("ROLE_STUDENTENRAT".equals(role)) {
                    isStudentenrat = true;
                }
            }
        }

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isStudentenrat", isStudentenrat);
        return "index";
    }

    @GetMapping("/registrierung")
    public String registrierung(Model model) {
        model.addAttribute("student", new Student());
        return "registrierung";
    }

    @PostMapping("/registrierung")
    public String registriereStudent(@ModelAttribute Student student, Model model) {
        // Passwort validieren
        if (!studentService.istPasswortGueltig(student.getPasswort())) {
            model.addAttribute("error", "Das Passwort muss mindestens 8 Zeichen lang sein.");
            return "registrierung";
        }
        studentService.registriereStudent(student);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/kalender")
    public String kalender() {
        return "kalender";
    }

    @GetMapping("/dokumente")
    public String dokumente() {
        return "dokumente";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }
}
