package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.model.Category;
import com.mine.hardware_pro.model.Supplier;
import com.mine.hardware_pro.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired SupplierRepository supplierRepository;

    @GetMapping
    public String listSuppliers(Model model) {
        List<Supplier> suppliers = supplierRepository.findAll(Sort.by("active").descending());
        model.addAttribute("suppliers", suppliers);
        return "pages/suppliers/supplier";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "pages/suppliers/supplier-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Supplier supplier, RedirectAttributes ra) {
        try {
            boolean isNew = (supplier.getIdSupplier() == null);
            supplierRepository.save(supplier);
            if (isNew) {
                ra.addFlashAttribute("success", "Proveedor creado exitosamente");
            } else {
                ra.addFlashAttribute("success", "Proveedor actualizado exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el proveedor");
        }
        return "redirect:/suppliers";
    }

    @GetMapping("/edit/{id}")
    public String editSupplier(@PathVariable("id") Long idSupplier, Model model, RedirectAttributes ra) {
        Supplier supplier = supplierRepository.findById(idSupplier).orElse(null);
        if (supplier == null) {
            ra.addFlashAttribute("error", "Proveedor no encontrado");
            return "redirect:/suppliers";
        }
        model.addAttribute("supplier", supplier);
        return "pages/suppliers/supplier-form";
    }

    @PostMapping("/toggle-active/{id}")
    @Transactional
    public String toggleActiveStatus(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            Supplier supplier = supplierRepository.findById(id).orElse(null);

            if (supplier == null) {
                ra.addFlashAttribute("error", "Proveedor no encontrado");
                return "redirect:/suppliers";
            }

            supplier.setActive(!supplier.isActive());
            supplierRepository.save(supplier);

            String status = supplier.isActive() ? "reactivado" : "inactivado";
            ra.addFlashAttribute("success", "Proveedor '" + supplier.getName() + "' ha sido " + status + ".");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al cambiar el estado del proveedor.");
        }
        return "redirect:/suppliers";
    }
}
