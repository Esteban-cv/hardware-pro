package com.mine.hardware_pro.dto;

import java.math.BigDecimal;

public class DashboardStatsDTO {
    private BigDecimal balance;
    private BigDecimal ganancia;
    private Long totalOrders;
    private Long salesToday;
    private BigDecimal salesTotal;
    private BigDecimal salesPercentageChange;

    // Constructor, getters y setters
    public DashboardStatsDTO() {}

    // Getters y Setters
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public BigDecimal getGanancia() { return ganancia; }
    public void setGanancia(BigDecimal ganancia) { this.ganancia = ganancia; }

    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }

    public Long getSalesToday() { return salesToday; }
    public void setSalesToday(Long salesToday) { this.salesToday = salesToday; }

    public BigDecimal getSalesTotal() { return salesTotal; }
    public void setSalesTotal(BigDecimal salesTotal) { this.salesTotal = salesTotal; }

    public BigDecimal getSalesPercentageChange() { return salesPercentageChange; }
    public void setSalesPercentageChange(BigDecimal salesPercentageChange) {
        this.salesPercentageChange = salesPercentageChange;
    }
}
