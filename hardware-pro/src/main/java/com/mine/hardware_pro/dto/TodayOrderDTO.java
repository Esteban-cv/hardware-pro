package com.mine.hardware_pro.dto;

public class TodayOrderDTO {
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String clientCity;
    private String status;

    // Constructor completo
    public TodayOrderDTO(String clientName, String clientEmail, String clientPhone,
                         String clientCity, String status) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.clientCity = clientCity;
        this.status = status;
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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
