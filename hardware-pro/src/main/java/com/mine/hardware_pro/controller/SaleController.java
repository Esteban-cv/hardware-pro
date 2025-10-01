package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.model.Article;
import com.mine.hardware_pro.model.Sale;
import com.mine.hardware_pro.model.SaleDetail;
import com.mine.hardware_pro.repository.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sales")
public class SaleController {

    @Autowired private SaleRepository saleRepository;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private SaleDetailRepository saleDetailRepository;

    @GetMapping
    public String listSales(Model model) {
        List<Sale> sales = saleRepository.findAll(Sort.by("date").descending());
        model.addAttribute("sales", sales);
        return "pages/sales/sale";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("sale", new Sale());
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("articles", articleRepository.findAll());
        return "pages/sales/sale-form";
    }

    @PostMapping("/save")
    @Transactional
    public String save(@ModelAttribute Sale sale,
                       @RequestParam("articleIds") List<Integer> articleIds,
                       @RequestParam("quantities") List<Integer> quantities,
                       @RequestParam("prices") List<BigDecimal> prices,
                       RedirectAttributes ra) {
        try {
            if (sale.getDate() == null) {
                sale.setDate(LocalDate.now());
            }
            // Validar stock antes de cualquier operación
            for (int i = 0; i < articleIds.size(); i++) {
                Article article = articleRepository.findById(articleIds.get(i)).orElseThrow();
                if (article.getQuantity() < quantities.get(i)) {
                    ra.addFlashAttribute("error", "Stock insuficiente para: " + article.getName());
                    return "redirect:/sales/form";
                }
            }

            sale.setDetails(new ArrayList<>());
            BigDecimal subTotal = BigDecimal.ZERO;
            for (int i = 0; i < articleIds.size(); i++) {
                subTotal = subTotal.add(prices.get(i).multiply(BigDecimal.valueOf(quantities.get(i))));
            }
            BigDecimal tax = subTotal.multiply(new BigDecimal("0.19")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = subTotal.add(tax);

            sale.setSubTotal(subTotal);
            sale.setTax(tax);
            sale.setTotal(total);

            Sale savedSale = saleRepository.save(sale);

            for (int i = 0; i < articleIds.size(); i++) {
                Article article = articleRepository.findById(articleIds.get(i)).orElseThrow();

                // ✅ RESTA EL STOCK DEL ARTÍCULO
                article.setQuantity(article.getQuantity() - quantities.get(i));
                articleRepository.save(article);

                SaleDetail detail = new SaleDetail();
                detail.setSale(savedSale);
                detail.setArticle(article);
                detail.setQuantity(quantities.get(i));
                detail.setUnitPrice(prices.get(i));
                detail.setTotal(prices.get(i).multiply(BigDecimal.valueOf(quantities.get(i))));
                savedSale.getDetails().add(detail);
            }
            ra.addFlashAttribute("success", "Venta registrada exitosamente. ID: " + savedSale.getIdSale());
            return "redirect:/sales";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al procesar la venta: " + e.getMessage());
            return "redirect:/sales/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Sale sale = saleRepository.findById(id).orElse(null);
        if (sale == null) {
            ra.addFlashAttribute("error", "Venta no encontrada");
            return "redirect:/sales";
        }
        Hibernate.initialize(sale.getDetails());
        model.addAttribute("sale", sale);
        return "pages/sales/edit-sale-form";
    }

    @PostMapping("/edit/{id}")
    @Transactional
    public String updateSale(@PathVariable("id") Long id, @ModelAttribute Sale saleFormData, RedirectAttributes ra) {
        try {
            Sale sale = saleRepository.findById(id).orElse(null);
            if (sale == null) {
                ra.addFlashAttribute("error", "Venta no encontrada");
                return "redirect:/sales";
            }
            sale.setDate(saleFormData.getDate());
            sale.setObservations(saleFormData.getObservations());
            saleRepository.save(sale);
            ra.addFlashAttribute("success", "Venta actualizada correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar la venta: " + e.getMessage());
        }
        return "redirect:/sales";
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            Sale sale = saleRepository.findById(id).orElse(null);
            if (sale == null) {
                ra.addFlashAttribute("error", "Venta no encontrada");
                return "redirect:/sales";
            }
            for (SaleDetail detail : sale.getDetails()) {
                Article article = detail.getArticle();
                // ✅ RESTAURA EL STOCK
                article.setQuantity(article.getQuantity() + detail.getQuantity());
                articleRepository.save(article);
            }
            saleRepository.deleteById(id);
            ra.addFlashAttribute("success", "Venta eliminada y stock restaurado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar la venta: " + e.getMessage());
        }
        return "redirect:/sales";
    }

    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewSaleDetails(@PathVariable("id") Long id) {
        try {
            Sale sale = saleRepository.findById(id).orElse(null);
            if (sale == null) {
                return ResponseEntity.notFound().build();
            }
            Hibernate.initialize(sale.getDetails());

            Map<String, Object> response = new HashMap<>();
            response.put("idSale", sale.getIdSale());
            if (sale.getDate() != null) {
                response.put("date", sale.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            response.put("clientName", sale.getClient() != null ? sale.getClient().getName() : "N/A");
            response.put("employeeName", sale.getEmployee() != null ? sale.getEmployee().getName() : "N/A");
            response.put("subTotal", sale.getSubTotal());
            response.put("tax", sale.getTax());
            response.put("total", sale.getTotal());
            response.put("observations", sale.getObservations());

            List<Map<String, Object>> detailsList = sale.getDetails().stream().map(detail -> {
                Map<String, Object> detailMap = new HashMap<>();
                detailMap.put("articleName", detail.getArticle().getName());
                detailMap.put("quantity", detail.getQuantity());
                detailMap.put("unitPrice", detail.getUnitPrice());
                detailMap.put("total", detail.getTotal());
                return detailMap;
            }).collect(Collectors.toList());
            response.put("details", detailsList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Los métodos privados 'calculateSubTotal' y 'processSaleDetails' ya no son necesarios
    // porque su lógica se integró en el método save().
}