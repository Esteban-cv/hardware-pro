package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.model.Entry;
import com.mine.hardware_pro.model.Issue;
import com.mine.hardware_pro.repository.*;
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
@RequestMapping("/issues")
public class IssueController {
    @Autowired private IssueRepository issueRepository;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired
    private EntryRepository entryRepository;

    @GetMapping
    public String listIssues(Model model) {
        List<Issue> issues = issueRepository.findAll(Sort.by("dateIssue").descending());
        model.addAttribute("issues", issues);
        return "pages/issues/issue";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("issue", new Issue());
        model.addAttribute("articles", articleRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("clients", clientRepository.findAll());
        return "pages/issues/issue-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Issue issue, RedirectAttributes ra) {
        try {
            boolean esNuevo = (issue.getIdIssue() == null);
            issueRepository.save(issue);

            // Mensaje según el caso
            if (esNuevo) {
                ra.addFlashAttribute("success", "Salida creada exitosamente");
            } else {
                ra.addFlashAttribute("success", "Salida actualizada exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar la salida");
        }
        return "redirect:/issues";
    }

    @GetMapping("/edit/{id}")
    public String editIssue(@PathVariable("id") Long idIssue, Model model, RedirectAttributes ra) {
        Issue issue = issueRepository.findById(idIssue).orElse(null);
        if (issue == null) {
            ra.addFlashAttribute("error", "Salida no encontrada");
            return "redirect:/issues";
        }
        model.addAttribute("issue", issue);
        model.addAttribute("articles", articleRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("clients", clientRepository.findAll());
        return "pages/issues/issue-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteIssue(@PathVariable("id") Long idIssue, RedirectAttributes ra) {
        issueRepository.deleteById(idIssue);
        ra.addFlashAttribute("success", "Salida eliminada exitosamente");
        return "redirect:/issue";
    }

    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewEntryDetails(@PathVariable("id") Long id) {
        try {
            Issue issue = issueRepository.findById(id).orElse(null);
            if (issue == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("type", "issue");
            response.put("idIssue", issue.getIdIssue());
            response.put("dateIssue", issue.getDateIssue());
            response.put("quantity", issue.getQuantity());
            response.put("observations", issue.getObservations());
            response.put("articleName", issue.getArticle() != null ? issue.getArticle().getName() : "N/A");
            response.put("employeeName", issue.getEmployee() != null ? issue.getEmployee().getName() : "N/A");
            response.put("clientName", issue.getClient() != null ? issue.getClient().getName() : "N/A");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
