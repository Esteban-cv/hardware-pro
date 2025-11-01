package com.mine.hardware_pro.service;

import com.mine.hardware_pro.dto.*;
import com.mine.hardware_pro.model.Sale;
import com.mine.hardware_pro.repository.PurchaseRepository;
import com.mine.hardware_pro.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {
    @Autowired private SaleRepository saleRepository;

    @Autowired private PurchaseRepository purchaseRepository;

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate firstDayOfLastMonth = firstDayOfMonth.minusMonths(1);

        // Total de ventas
        BigDecimal totalSales = saleRepository.sumAllSales();

        // Total de compras
        BigDecimal totalPurchases = purchaseRepository.sumAllPurchases();

        // Balance = Ventas - Compras
        BigDecimal balance = totalSales.subtract(totalPurchases);
        stats.setBalance(balance);

        // Ganancia (considerando que el subtotal es lo que ganamos antes de impuestos)
        stats.setGanancia(totalSales.multiply(BigDecimal.valueOf(0.30))); // Asumiendo 30% de margen

        // Total de órdenes
        stats.setTotalOrders(saleRepository.count());

        // Ventas de hoy
        stats.setSalesToday(saleRepository.countSalesToday(today));

        // Total ventas del mes actual
        BigDecimal currentMonthSales = saleRepository.sumSalesByMonth(
                today.getMonthValue(), today.getYear()
        );
        stats.setSalesTotal(currentMonthSales);

        // Ventas del mes anterior
        BigDecimal lastMonthSales = saleRepository.sumSalesByMonth(
                firstDayOfLastMonth.getMonthValue(), firstDayOfLastMonth.getYear()
        );

        // Calcular porcentaje de cambio
        if (lastMonthSales.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal change = currentMonthSales.subtract(lastMonthSales)
                    .divide(lastMonthSales, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            stats.setSalesPercentageChange(change);
        } else {
            stats.setSalesPercentageChange(BigDecimal.valueOf(100));
        }

        return stats;
    }

    public List<MonthlySalesDTO> getMonthlySalesData() {
        int currentYear = LocalDate.now().getYear();
        List<Object[]> results = saleRepository.getMonthlySales(currentYear);

        List<MonthlySalesDTO> monthlySales = new ArrayList<>();
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        // Inicializar todos los meses con 0
        for (int i = 0; i < 12; i++) {
            monthlySales.add(new MonthlySalesDTO(monthNames[i], BigDecimal.ZERO));
        }

        // Llenar con datos reales
        for (Object[] result : results) {
            int month = ((Number) result[0]).intValue();
            BigDecimal total = (BigDecimal) result[1];
            monthlySales.set(month - 1, new MonthlySalesDTO(monthNames[month - 1], total));
        }

        return monthlySales;
    }

    public List<TodayOrderDTO> getTodayOrders() {
        LocalDate today = LocalDate.now();
        List<Sale> sales = saleRepository.findTodaySales(today);

        List<TodayOrderDTO> orders = new ArrayList<>();
        for (Sale sale : sales) {
            orders.add(new TodayOrderDTO(
                    sale.getClient().getName(),
                    sale.getClient().getEmail(),
                    sale.getClient().getPhone(),
                    sale.getClient().getAddress(),
                    sale.getClient().isActive() ? "Active" : "Inactive"
            ));
        }

        return orders;
    }

    public List<TopClientDTO> getTopClients() {
        List<Object[]> results = saleRepository.findTopClientsByTotalSpent();

        List<TopClientDTO> topClients = new ArrayList<>();

        // Tomar solo los primeros 10 clientes
        int limit = Math.min(10, results.size());

        for (int i = 0; i < limit; i++) {
            Object[] result = results.get(i);
            topClients.add(new TopClientDTO(
                    (String) result[0],        // clientName
                    (String) result[1],        // clientEmail
                    (String) result[2],        // clientPhone
                    (String) result[3],        // clientCity
                    (Boolean) result[4],       // clientActive
                    ((Number) result[5]).longValue(),           // totalOrders
                    (BigDecimal) result[6]     // totalSpent
            ));
        }

        return topClients;
    }

    public EarningsDTO getEarnings() {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        int lastMonth = today.minusMonths(1).getMonthValue();
        int lastMonthYear = today.minusMonths(1).getYear();

        // Ventas de hoy
        BigDecimal todaySales = saleRepository.sumSalesByDate(today);

        // Ventas del mes actual
        BigDecimal thisMonthSales = saleRepository.sumSalesByMonthAndYear(currentMonth, currentYear);

        // Ventas del año
        BigDecimal thisYearSales = saleRepository.sumSalesByYear(currentYear);

        // Ventas del mes pasado para calcular cambio
        BigDecimal lastMonthSales = saleRepository.sumSalesByMonthAndYear(lastMonth, lastMonthYear);

        // Calcular porcentaje de cambio
        BigDecimal percentageChange = BigDecimal.ZERO;
        if (lastMonthSales.compareTo(BigDecimal.ZERO) > 0) {
            percentageChange = thisMonthSales.subtract(lastMonthSales)
                    .divide(lastMonthSales, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        } else if (thisMonthSales.compareTo(BigDecimal.ZERO) > 0) {
            percentageChange = BigDecimal.valueOf(100);
        }

        return new EarningsDTO(todaySales, thisMonthSales, thisYearSales, percentageChange);
    }
}
