package org.example.krutishe_za_vsih.controller;

import org.example.krutishe_za_vsih.model.ReportTemplate;
import org.example.krutishe_za_vsih.service.CrmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CrmController {

    @Autowired
    private CrmService crmService;

    @Autowired
    private ApplicationContext context;

    @GetMapping("/")
    public String showMainClientPage(Model model) {
        model.addAttribute("programs", crmService.getAvailablePrograms());
        model.addAttribute("addons", crmService.getAvailableAddons());
        return "index";
    }

    @GetMapping("/animators")
    public String showAnimators(Model model) {
        model.addAttribute("animators", crmService.getTeam());
        return "animators";
    }

    @GetMapping("/report")
    public String showReportPage(Model model) {
        ReportTemplate template = context.getBean(ReportTemplate.class);
        model.addAttribute("reportBlank", template.getBlankId());
        model.addAttribute("animators", crmService.getTeam());
        return "report";
    }
}