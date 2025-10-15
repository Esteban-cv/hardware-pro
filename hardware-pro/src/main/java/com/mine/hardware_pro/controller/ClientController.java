package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.model.Article;
import com.mine.hardware_pro.model.Category;
import com.mine.hardware_pro.model.Client;
import com.mine.hardware_pro.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired ClientRepository clientRepository;

    @GetMapping
    public String listClients(Model model){
        List<Client> clients = clientRepository.findAll(Sort.by("idClient").descending());
        model.addAttribute("clients",clients);
        return "pages/clients/client";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("client",new Client());
        return  "pages/clients/client-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Client client, RedirectAttributes ra) {
        try {
            boolean isNew = (client.getIdClient() == null);
            if (client.getDocument() != null && !client.getDocument().isBlank()) {
                if (isNew && clientRepository.existsByDocument(client.getDocument())) {
                    ra.addFlashAttribute("error", "El documento '" + client.getDocument() + "' ya está registrado.");
                    ra.addFlashAttribute("client", client);
                    return "redirect:/clients/form";
                }
            }
            clientRepository.save(client);

            if (isNew) {
                ra.addFlashAttribute("success", "Cliente creado exitosamente");
            } else {
                ra.addFlashAttribute("success", "Cliente actualizado exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el cliente");
        }
        return "redirect:/clients";
    }

    @GetMapping("/edit/{id}")
    public String editClient(@PathVariable("id") Long idClient, Model model, RedirectAttributes ra) {
        Client client = clientRepository.findById(idClient).orElse(null);
        if (client == null) {
            ra.addFlashAttribute("error", "Cliente no encontrado");
            return "redirect:/clients";
        }
        model.addAttribute("client", client);
        return "pages/clients/client-form";
    }

    @PostMapping("/toggle-active/{id}")
    @Transactional
    public String toggleActiveStatus(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            Client client = clientRepository.findById(id).orElse(null);

            if (client == null) {
                ra.addFlashAttribute("error", "Cliente no encontrado");
                return "redirect:/clients";
            }

            client.setActive(!client.isActive());
            clientRepository.save(client);

            String status = client.isActive() ? "reactivado" : "inactivado";
            ra.addFlashAttribute("success", "Cliente '" + client.getName() + "' ha sido " + status + ".");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al cambiar el estado del cliente.");
        }

        return "redirect:/clients";
    }

    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewClientDetails(@PathVariable("id") Long id) {
        try {
            Client client = clientRepository.findById(id).orElse(null);
            if (client == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("type", "client");
            response.put("idClient", client.getIdClient());
            response.put("name", client.getName());
            response.put("document", client.getDocument());
            response.put("email", client.getEmail() != null ? client.getEmail() : "N/A");
            response.put("address", client.getAddress() != null ? client.getAddress() : "N/A");
            response.put("phone", client.getPhone() != null ? client.getPhone() : "N/A");
            response.put("rut", client.getRut() != null ? client.getRut() : "N/A");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
