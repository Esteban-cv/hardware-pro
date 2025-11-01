package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.dto.*;
import com.mine.hardware_pro.service.DashboardService;
import com.mine.hardware_pro.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class DashboardController {
    @Autowired private DashboardService dashboardService;
    @Autowired private SettingService settingService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("currencySymbol", settingService.getSettingValue("CURRENCY_SYMBOL"));
        return "pages/home";
    }

    @GetMapping("/api/dashboard/monthly-sales")
    @ResponseBody
    public List<MonthlySalesDTO> getMonthlySales() {
        return dashboardService.getMonthlySalesData();
    }

    @GetMapping("/api/dashboard/today-orders")
    @ResponseBody
    public List<TodayOrderDTO> getTodayOrders() {
        return dashboardService.getTodayOrders();
    }

    @GetMapping("/api/dashboard/top-clients")
    @ResponseBody
    public List<TopClientDTO> getTopClients() {
        return dashboardService.getTopClients();
    }

    // DashboardController.java - Agregar este método

    @GetMapping("/api/dashboard/earnings")
    @ResponseBody
    public EarningsDTO getEarnings() {
        return dashboardService.getEarnings();
    }
}
