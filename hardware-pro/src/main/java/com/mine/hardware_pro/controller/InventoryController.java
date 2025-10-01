package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.model.Article;
import com.mine.hardware_pro.model.Inventory;
import com.mine.hardware_pro.model.Location;
import com.mine.hardware_pro.repository.ArticleRepository;
import com.mine.hardware_pro.repository.InventoryRepository;
import com.mine.hardware_pro.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/inventories")
public class InventoryController {

    @Autowired private InventoryRepository inventoryRepository;

    @Autowired private LocationRepository locationRepository;

    @Autowired private ArticleRepository articleRepository;

    @GetMapping
    public String listInventories(Model model){
        List<Inventory> inventories = inventoryRepository.findAll(Sort.by("idInventory").descending());
        model.addAttribute("inventories", inventories);
        return "pages/inventories/inventory";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("inventory", new Inventory());
        model.addAttribute("articles", articleRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        return "pages/inventories/inventory-form";
    }

    @PostMapping("/save")
    @Transactional
    public String save(@ModelAttribute Inventory inventory, RedirectAttributes ra) {
        try {
            if (inventory.getArticle() == null || inventory.getArticle().getIdArticle() == null) {
                ra.addFlashAttribute("error", "Debe seleccionar un artículo.");
                return "redirect:/inventories/form";
            }
            if (inventory.getLocation() == null || inventory.getLocation().getIdLocation() == null) {
                ra.addFlashAttribute("error", "Debe seleccionar una ubicación.");
                return "redirect:/inventories/form";
            }

            Article article = articleRepository.findById(inventory.getArticle().getIdArticle()).orElseThrow();
            Location location = locationRepository.findById(inventory.getLocation().getIdLocation()).orElseThrow();
            inventory.setArticle(article);
            inventory.setLocation(location);

            boolean isNew = (inventory.getIdInventory() == null);

            
            inventory.setUpdatingDate(LocalDate.now());


            inventoryRepository.save(inventory);
            ra.addFlashAttribute("success", isNew ? "Registro de inventario creado." : "Registro de inventario actualizado.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el registro de inventario.");
            return "redirect:/inventories/form";
        }
        return "redirect:/inventories";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer idInventory, Model model, RedirectAttributes ra) {
        Inventory inventory = inventoryRepository.findById(idInventory).orElse(null);
        if (inventory == null) {
            ra.addFlashAttribute("error", "Inventario no encontrado.");
            return "redirect:/inventory";
        }
        model.addAttribute("inventory", inventory);
        model.addAttribute("articles", articleRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        return "pages/inventories/inventory-form";
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable("id") Integer idInventory, RedirectAttributes ra) {
        try {
            Inventory inventory = inventoryRepository.findById(idInventory).orElse(null);
            if (inventory == null) {
                ra.addFlashAttribute("error", "Registro de inventario no encontrado.");
                return "redirect:/inventories";
            }
            Article article = inventory.getArticle();
            int quantityToRemove = inventory.getCurrentStock();
            article.setQuantity(article.getQuantity() - quantityToRemove);
            articleRepository.save(article);

            inventoryRepository.deleteById(idInventory);
            ra.addFlashAttribute("success", "Inventario eliminado y stock del artículo actualizado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar el registro de inventario.");
        }
        return "redirect:/inventories";
    }

    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewInventoryDetails(@PathVariable("id") Integer id) {
        return inventoryRepository.findById(id)
                .map(inventory -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("idInventory", inventory.getIdInventory());
                    response.put("articleName", inventory.getArticle() != null ? inventory.getArticle().getName() : "N/A");
                    response.put("locationName", inventory.getLocation() != null ? inventory.getLocation().getName() : "N/A");
                    response.put("currentStock", inventory.getCurrentStock());
                    response.put("minimumStock", inventory.getMinimumStock());
                    if (inventory.getUpdatingDate() != null) {
                        response.put("updatingDate", inventory.getUpdatingDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    }
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
