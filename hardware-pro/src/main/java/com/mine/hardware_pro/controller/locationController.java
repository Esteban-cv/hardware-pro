package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.model.Location;
import com.mine.hardware_pro.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/locations")
public class locationController {

    @Autowired LocationRepository locationRepository;

    @GetMapping
    public String listLocations(Model model) {
        List<Location> locations = locationRepository.findAll(Sort.by("idLocation").descending());
        model.addAttribute("locations", locations);
        return "pages/locations/location";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("location", new Location());
        return "pages/locations/location-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Location location, RedirectAttributes ra) {
        try {
            boolean isNew = (location.getIdLocation() == null);
            locationRepository.save(location);
            if (isNew) {
                ra.addFlashAttribute("success", "Ubicación creada exitosamente");
            } else {
                ra.addFlashAttribute("success", "Ubicación actualizada exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar la ubicación");
        }
        return "redirect:/locations";
    }

    @GetMapping("/edit/{id}")
    public String editLocation(@PathVariable("id") Integer idLocation, Model model, RedirectAttributes ra) {
        Location location = locationRepository.findById(idLocation).orElse(null);
        if (location == null) {
            ra.addFlashAttribute("error", "Ubicación no encontrado");
            return "redirect:/locations";
        }
        model.addAttribute("location", location);
        return "pages/locations/location-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteLocation(@PathVariable("id") Integer idLocation, RedirectAttributes ra) {
        try {
            locationRepository.deleteById(idLocation);
            ra.addFlashAttribute("success", "Ubicación eliminada exitosamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Hubo un error al eliminar la Ubicación.");
        }
        return "redirect:/locations";
    }
}
