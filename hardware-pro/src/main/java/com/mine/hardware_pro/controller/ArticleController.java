package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.model.Article;
import com.mine.hardware_pro.model.Sale;
import com.mine.hardware_pro.model.SaleDetail;
import com.mine.hardware_pro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/articles")
public class ArticleController {
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private CategoryRepository  categoryRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private SupplierRepository supplierRepository;

    @GetMapping
    public String listArticles(Model model){
        List<Article>  articles = articleRepository.findAll(Sort.by("idArticle").ascending());
        model.addAttribute("articles", articles);
        return "pages/articles/article";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        model.addAttribute("suppliers", supplierRepository.findAll());
        return "pages/articles/article-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Article article, RedirectAttributes ra) {
        try {
            boolean isNew = (article.getIdArticle() == null);
            articleRepository.save(article);
            if (isNew) {
                ra.addFlashAttribute("success", "Artículo creado exitosamente");
            } else {
                ra.addFlashAttribute("success", "Artículo actualizado exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el artículo");
        }
        return "redirect:/articles";
    }

    @GetMapping("/edit/{id}")
    public String editArticle(@PathVariable("id") Integer idArticle, Model model, RedirectAttributes ra) {
        Article article = articleRepository.findById(idArticle).orElse(null);
        if (article == null) {
            ra.addFlashAttribute("error", "Artículo no encontrado");
            return "redirect:/articles";
        }
        model.addAttribute("article", article);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        model.addAttribute("suppliers", supplierRepository.findAll());
        return "pages/articles/article-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteArticle(@PathVariable("id") Integer idArticle, RedirectAttributes ra) {
        try {
            articleRepository.deleteById(idArticle);
            ra.addFlashAttribute("success", "Artículo eliminado exitosamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Hubo un error al eliminar el artículo.");
        }
        return "redirect:/articles";
    }

    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewArticleDetails(@PathVariable("id") Integer id) {
        try {
            Article article = articleRepository.findById(id).orElse(null);
            if (article == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("type", "article");
            response.put("idArticle", article.getIdArticle());
            response.put("name", article.getName());
            response.put("code", article.getCode());
            response.put("price", article.getPrice());
            response.put("quantity", article.getQuantity());
            response.put("categoryName", article.getCategory() != null ? article.getCategory().getName() : "N/A");
            response.put("unitName", article.getUnit() != null ? article.getUnit().getName() : "N/A");
            response.put("supplierName", article.getSupplier() != null ? article.getSupplier().getName() : "N/A");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
