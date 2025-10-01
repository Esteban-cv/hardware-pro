package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.model.Category;
import com.mine.hardware_pro.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    @Autowired CategoryRepository categoryRepository;

    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryRepository.findAll(Sort.by("idCategory").descending());
        model.addAttribute("categories", categories);
        return "pages/categories/category";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("category", new Category());
        return "pages/categories/category-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Category category, RedirectAttributes ra) {
        try {
            boolean isNew = (category.getIdCategory() == null);
            categoryRepository.save(category);
            if (isNew) {
                ra.addFlashAttribute("success", "Categoría creada exitosamente");
            } else {
                ra.addFlashAttribute("success", "Categoría actualizada exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar la categoría");
        }
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable("id") Integer idCategory, Model model, RedirectAttributes ra) {
        Category category = categoryRepository.findById(idCategory).orElse(null);
        if (category == null) {
            ra.addFlashAttribute("error", "Categoría no encontrada");
            return "redirect:/categories";
        }
        model.addAttribute("category", category);
        return "pages/categories/category-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer idCategory, RedirectAttributes ra) {
        try {
            categoryRepository.deleteById(idCategory);
            ra.addFlashAttribute("success", "Categoría eliminada exitosamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Hubo un error al eliminar la categoría.");
        }
        return "redirect:/categories";
    }
}
