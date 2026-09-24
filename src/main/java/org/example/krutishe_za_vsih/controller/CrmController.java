package org.example.krutishe_za_vsih.controller;

import org.example.krutishe_za_vsih.service.CrmService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CrmController {

    private final CrmService crmService;

    public CrmController(CrmService crmService) {
        this.crmService = crmService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("programs", crmService.getAllPrograms());
        return "index";
    }

    @GetMapping("/animators")
    public String animators(Model model) {
        model.addAttribute("animators", crmService.getAllAnimators());
        return "animators";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/reports")
    public String reports() {

        return "report";
    }
}