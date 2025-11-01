package com.mine.hardware_pro.dto;

import java.math.BigDecimal;

public class MonthlySalesDTO {
    private String month;
    private BigDecimal total;

    public MonthlySalesDTO(String month, BigDecimal total) {
        this.month = month;
        this.total = total;
    }

    // Getters y Setters
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
