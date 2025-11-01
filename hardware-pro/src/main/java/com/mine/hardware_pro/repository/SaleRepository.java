package com.mine.hardware_pro.repository;

import com.mine.hardware_pro.model.Purchase;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mine.hardware_pro.model.Sale;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByDateBetween(LocalDate startDate, LocalDate endDate, Sort sort);

    // Total de ventas de hoy
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.date = :today")
    Long countSalesToday(@Param("today") LocalDate today);

    // Total de ventas (monto) de hoy
    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s WHERE s.date = :today")
    BigDecimal sumTotalToday(@Param("today") LocalDate today);

    // Total de todas las ventas
    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s")
    BigDecimal sumAllSales();

    // Ventas del mes actual
    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s WHERE MONTH(s.date) = :month AND YEAR(s.date) = :year")
    BigDecimal sumSalesByMonth(@Param("month") int month, @Param("year") int year);

    // Ventas por mes para gráfico
    @Query("SELECT MONTH(s.date) as month, COALESCE(SUM(s.total), 0) as total " +
            "FROM Sale s WHERE YEAR(s.date) = :year " +
            "GROUP BY MONTH(s.date) ORDER BY MONTH(s.date)")
    List<Object[]> getMonthlySales(@Param("year") int year);

    // Ventas de hoy con información del cliente
    @Query("SELECT s FROM Sale s WHERE s.date = :today ORDER BY s.createdAt DESC")
    List<Sale> findTodaySales(@Param("today") LocalDate today);

    // TOP CLIENTES QUE MÁS HAN COMPRADO (POR MONTO TOTAL)
    @Query("SELECT s.client.name as clientName, " +
            "s.client.email as clientEmail, " +
            "s.client.phone as clientPhone, " +
            "s.client.address as clientCity, " +
            "s.client.active as clientActive, " +
            "COUNT(s) as totalOrders, " +
            "SUM(s.total) as totalSpent " +
            "FROM Sale s " +
            "GROUP BY s.client.id, s.client.name, s.client.email, s.client.phone, s.client.address, s.client.active " +
            "ORDER BY SUM(s.total) DESC")
    List<Object[]> findTopClientsByTotalSpent();

    // Ventas totales del año
    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s WHERE YEAR(s.date) = :year")
    BigDecimal sumSalesByYear(@Param("year") int year);

    // Ventas totales del mes
    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s " +
            "WHERE MONTH(s.date) = :month AND YEAR(s.date) = :year")
    BigDecimal sumSalesByMonthAndYear(@Param("month") int month, @Param("year") int year);

    // Ventas del día
    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s WHERE s.date = :date")
    BigDecimal sumSalesByDate(@Param("date") LocalDate date);
}
