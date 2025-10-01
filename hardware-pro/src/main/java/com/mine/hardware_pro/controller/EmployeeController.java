package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.model.Employee;
import com.mine.hardware_pro.repository.EmployeeRepository;
import com.mine.hardware_pro.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired EmployeeRepository employeeRepository;
    @Autowired RoleRepository roleRepository;

    @GetMapping
    public String listEmployees(Model model) {
        List<Employee> employees =  employeeRepository.findAll(Sort.by("idEmployee").descending());
        model.addAttribute("employees", employees);
        return "pages/employees/employee";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("roles", roleRepository.findAll());
        return  "pages/employees/employee-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Employee employee, RedirectAttributes ra) {
        try {
            boolean isNew = (employee.getIdEmployee() == null);
            employeeRepository.save(employee);
            if (isNew) {
                ra.addFlashAttribute("success", "Empleado creado exitosamente");
            } else {
                ra.addFlashAttribute("success", "Empleado actualizado exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el Empleado");
        }
        return "redirect:/employees";
    }

    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable("id") Long idEmployee, Model model, RedirectAttributes ra) {
        Employee employee = employeeRepository.findById(idEmployee).orElse(null);
        if (employee == null) {
            ra.addFlashAttribute("error", "Empleado no encontrado");
            return "redirect:/employees";
        }
        model.addAttribute("employee", employee);
        model.addAttribute("roles", roleRepository.findAll());
        return "pages/employees/employee-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") Long idEmployee, RedirectAttributes ra) {
        try {
            employeeRepository.deleteById(idEmployee);
            ra.addFlashAttribute("success", "Empleado eliminado exitosamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Hubo un error al eliminar el empleado.");
        }
        return "redirect:/employees";
    }
}
