package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.model.Article;
import com.mine.hardware_pro.model.Entry;
import com.mine.hardware_pro.repository.ArticleRepository;
import com.mine.hardware_pro.repository.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/entries")
public class EntryController {

    @Autowired EntryRepository entryRepository;
    @Autowired ArticleRepository articleRepository;

    @GetMapping
    public String listEntries(Model model) {
        List<Entry> entries = entryRepository.findAll(Sort.by("idEntry").descending());
        model.addAttribute("entries", entries);
        return "pages/entries/entry";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("entry", new Entry());
        model.addAttribute("articles", articleRepository.findAll());
        return "pages/entries/entry-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Entry entry, RedirectAttributes ra) {
        try {
            boolean isNew = (entry.getIdEntry() == null);
            entryRepository.save(entry);
            if (isNew) {
                ra.addFlashAttribute("success", "Entrada creada exitosamente");
            } else {
                ra.addFlashAttribute("success", "Entrada actualizada exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar la Entrada");
        }
        return "redirect:/entries";
    }

    @GetMapping("/edit/{id}")
    public String editEntry(@PathVariable("id") Integer idEntry, Model model, RedirectAttributes ra) {
        Entry entry = entryRepository.findById(idEntry).orElse(null);
        if (entry == null) {
            ra.addFlashAttribute("error", "Entrada no encontrada");
            return "redirect:/entries";
        }
        model.addAttribute("entry", entry);
        model.addAttribute("articles", articleRepository.findAll());
        return "pages/entries/entry-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteEntry(@PathVariable("id") Integer idEntry, RedirectAttributes ra) {
        try {
            entryRepository.deleteById(idEntry);
            ra.addFlashAttribute("success", "Entrada eliminada exitosamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Hubo un error al eliminar la Entrada.");
        }
        return "redirect:/entries";
    }

    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewEntryDetails(@PathVariable("id") Integer id) {
        try {
            Entry entry = entryRepository.findById(id).orElse(null);
            if (entry == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("type", "entry");
            response.put("idEntry", entry.getIdEntry());
            response.put("dateEntry", entry.getDateEntry());
            response.put("quantity", entry.getQuantity());
            response.put("observations", entry.getObservations());
            response.put("articleName", entry.getArticle() != null ? entry.getArticle().getName() : "N/A");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
