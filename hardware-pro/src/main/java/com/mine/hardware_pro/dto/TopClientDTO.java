package com.mine.hardware_pro.dto;

import java.math.BigDecimal;

public class TopClientDTO {
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String clientCity;
    private boolean clientActive;
    private Long totalOrders;
    private BigDecimal totalSpent;

    public TopClientDTO(String clientName, String clientEmail, String clientPhone,
                        String clientCity, boolean clientActive, Long totalOrders, BigDecimal totalSpent) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.clientCity = clientCity;
        this.clientActive = clientActive;
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
    }

    // Getters y Setters
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public String getClientCity() { return clientCity; }
    public void setClientCity(String clientCity) { this.clientCity = clientCity; }

    public boolean isClientActive() { return clientActive; }
    public void setClientActive(boolean clientActive) { this.clientActive = clientActive; }

    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }

    public BigDecimal getTotalSpent() { return totalSpent; }
    public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }

    public String getStatus() {
        return clientActive ? "Active" : "Inactive";
    }
}
