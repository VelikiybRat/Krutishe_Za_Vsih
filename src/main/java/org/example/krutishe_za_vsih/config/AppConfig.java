package org.example.krutishe_za_vsih.config;

import org.example.krutishe_za_vsih.model.ReportTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {

    @Bean
    @Scope("prototype")
    public ReportTemplate reportTemplate() {
        return new ReportTemplate();
    }
}