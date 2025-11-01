package com.mine.hardware_pro.dto;

import java.math.BigDecimal;

public class EarningsDTO {
    private BigDecimal today;
    private BigDecimal thisMonth;
    private BigDecimal thisYear;
    private BigDecimal percentageChange;

    public EarningsDTO() {}

    public EarningsDTO(BigDecimal today, BigDecimal thisMonth, BigDecimal thisYear, BigDecimal percentageChange) {
        this.today = today;
        this.thisMonth = thisMonth;
        this.thisYear = thisYear;
        this.percentageChange = percentageChange;
    }

    // Getters y Setters
    public BigDecimal getToday() { return today; }
    public void setToday(BigDecimal today) { this.today = today; }

    public BigDecimal getThisMonth() { return thisMonth; }
    public void setThisMonth(BigDecimal thisMonth) { this.thisMonth = thisMonth; }

    public BigDecimal getThisYear() { return thisYear; }
    public void setThisYear(BigDecimal thisYear) { this.thisYear = thisYear; }

    public BigDecimal getPercentageChange() { return percentageChange; }
    public void setPercentageChange(BigDecimal percentageChange) {
        this.percentageChange = percentageChange;
    }
}
