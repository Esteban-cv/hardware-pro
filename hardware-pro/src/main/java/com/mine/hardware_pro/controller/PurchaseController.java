package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.model.Article;
import com.mine.hardware_pro.model.Purchase;
import com.mine.hardware_pro.model.PurchaseDetail;
import com.mine.hardware_pro.repository.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired private PurchaseRepository purchaseRepository;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private EmployeeRepository employeeRepository;

    @GetMapping
    public String listPurchases(Model model, @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Sort sort = Sort.by("date").descending();
        List<Purchase> purchases = (startDate != null && endDate != null) ?
                purchaseRepository.findByDateBetween(startDate, endDate, sort) :
                purchaseRepository.findAll(sort);
        model.addAttribute("purchases", purchases);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "pages/purchases/purchase";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("purchase", new Purchase());
        model.addAttribute("allArticles", articleRepository.findAll());
        model.addAttribute("allSuppliers", supplierRepository.findAll());
        model.addAttribute("allEmployees", employeeRepository.findAll());
        return "pages/purchases/purchase-form";
    }

    @PostMapping("/save")
    @Transactional
    public String save(@ModelAttribute Purchase purchase,
                       @RequestParam("articleIds") List<Integer> articleIds,
                       @RequestParam("quantities") List<Integer> quantities,
                       @RequestParam("unitPrices") List<BigDecimal> unitPrices,
                       RedirectAttributes ra) {
        try {
            if (purchase.getDate() == null) {
                purchase.setDate(LocalDate.now());
            }
            purchase.setDetails(new ArrayList<>());

            BigDecimal subTotal = BigDecimal.ZERO;
            for (int i = 0; i < articleIds.size(); i++) {
                subTotal = subTotal.add(unitPrices.get(i).multiply(BigDecimal.valueOf(quantities.get(i))));
            }
            BigDecimal tax = subTotal.multiply(new BigDecimal("0.19")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = subTotal.add(tax);

            purchase.setSubTotal(subTotal);
            purchase.setTax(tax);
            purchase.setTotal(total);

            Purchase savedPurchase = purchaseRepository.save(purchase);

            for (int i = 0; i < articleIds.size(); i++) {
                final Integer currentArticleId = articleIds.get(i);
                Article article = articleRepository.findById(currentArticleId)
                        .orElseThrow(() -> new RuntimeException("Artículo no encontrado con ID: " + currentArticleId));

                // ✅ AUMENTA EL STOCK DEL ARTÍCULO
                article.setQuantity(article.getQuantity() + quantities.get(i));
                articleRepository.save(article);

                PurchaseDetail detail = new PurchaseDetail();
                detail.setPurchase(savedPurchase);
                detail.setArticle(article);
                detail.setQuantity(quantities.get(i));
                detail.setUnitPrice(unitPrices.get(i));
                BigDecimal detailTotal = unitPrices.get(i).multiply(BigDecimal.valueOf(quantities.get(i)));
                detail.setTotal(detailTotal);
                savedPurchase.getDetails().add(detail);
            }

            ra.addFlashAttribute("success", "Compra registrada exitosamente. ID: " + savedPurchase.getIdPurchase());
            return "redirect:/purchases";
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al registrar la compra: " + e.getMessage());
            return "redirect:/purchases/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Purchase purchase = purchaseRepository.findById(id).orElse(null);
        if (purchase == null) {
            ra.addFlashAttribute("error", "Compra no encontrada");
            return "redirect:/purchases";
        }
        Hibernate.initialize(purchase.getDetails());
        model.addAttribute("purchase", purchase);
        return "pages/purchases/edit-purchase-form";
    }

    @PostMapping("/edit/{id}")
    @Transactional
    public String updatePurchase(@PathVariable("id") Long id,
                                 @ModelAttribute Purchase purchaseFormData,
                                 RedirectAttributes ra) {
        try {
            Purchase purchase = purchaseRepository.findById(id).orElse(null);
            if (purchase == null) {
                ra.addFlashAttribute("error", "Compra no encontrada");
                return "redirect:/purchases";
            }
            purchase.setDate(purchaseFormData.getDate());

            purchaseRepository.save(purchase);
            ra.addFlashAttribute("success", "Compra actualizada correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar la compra: " + e.getMessage());
        }
        return "redirect:/purchases";
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable("id") Long idPurchase, RedirectAttributes ra) {
        try {
            Purchase purchase = purchaseRepository.findById(idPurchase).orElse(null);
            if (purchase == null) {
                ra.addFlashAttribute("error", "Compra no encontrada");
                return "redirect:/purchases";
            }
            for (PurchaseDetail detail : purchase.getDetails()) {
                Article article = detail.getArticle();
                if (article != null) {
                    // ✅ RESTA EL STOCK
                    int newQuantity = article.getQuantity() - detail.getQuantity();
                    article.setQuantity(newQuantity);
                    articleRepository.save(article);
                }
            }
            purchaseRepository.deleteById(idPurchase);
            ra.addFlashAttribute("success", "Compra eliminada y stock actualizado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar la compra: " + e.getMessage());
        }
        return "redirect:/purchases";
    }

    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewPurchaseDetails(@PathVariable("id") Long id) {
        try {
            Purchase purchase = purchaseRepository.findById(id).orElse(null);
            if (purchase == null) {
                return ResponseEntity.notFound().build();
            }
            Hibernate.initialize(purchase.getDetails());

            Map<String, Object> response = new HashMap<>();
            response.put("idPurchase", purchase.getIdPurchase());
            if (purchase.getDate() != null) {
                response.put("date", purchase.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            response.put("supplierName", purchase.getSupplier() != null ? purchase.getSupplier().getName() : "N/A");
            response.put("employeeName", purchase.getEmployee() != null ? purchase.getEmployee().getName() : "N/A");
            response.put("subTotal", purchase.getSubTotal());
            response.put("tax", purchase.getTax());
            response.put("total", purchase.getTotal());

            List<Map<String, Object>> detailsList = purchase.getDetails().stream().map(detail -> {
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
}