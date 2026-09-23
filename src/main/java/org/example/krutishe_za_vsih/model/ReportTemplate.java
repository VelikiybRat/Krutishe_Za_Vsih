package org.example.krutishe_za_vsih.model;

import java.util.UUID;

public class ReportTemplate {
    private String blankId;

    public ReportTemplate() {
        this.blankId = "REP-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }

    public String getBlankId() { return blankId; }
}