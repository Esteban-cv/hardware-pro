package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.model.Employee;
import com.mine.hardware_pro.model.User;
import com.mine.hardware_pro.repository.EmployeeRepository;
import com.mine.hardware_pro.repository.RoleRepository;
import com.mine.hardware_pro.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired EmployeeRepository employeeRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

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
    @Transactional
    public String save(@ModelAttribute Employee employeeFromForm, RedirectAttributes ra) {
        try {
            boolean isNew = (employeeFromForm.getIdEmployee() == null);

            if (isNew) {
                User user = employeeFromForm.getUser();
                user.setFirstName(employeeFromForm.getFirstName());
                user.setLastName(employeeFromForm.getLastName());
                user.setActive(true);
                user.setPassword(passwordEncoder.encode(user.getPassword()));

                employeeRepository.save(employeeFromForm);
                ra.addFlashAttribute("success", "Empleado creado exitosamente");

            } else {
                Employee employeeToUpdate = employeeRepository.findById(employeeFromForm.getIdEmployee())
                        .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

                employeeToUpdate.setFirstName(employeeFromForm.getFirstName());
                employeeToUpdate.setLastName(employeeFromForm.getLastName());
                employeeToUpdate.setDocument(employeeFromForm.getDocument());
                employeeToUpdate.setPhone(employeeFromForm.getPhone());
                employeeToUpdate.setAddress(employeeFromForm.getAddress());

                User userToUpdate = employeeToUpdate.getUser();
                userToUpdate.setFirstName(employeeFromForm.getFirstName());
                userToUpdate.setLastName(employeeFromForm.getLastName());
                userToUpdate.setEmail(employeeFromForm.getUser().getEmail());
                userToUpdate.setRole(employeeFromForm.getUser().getRole());

                employeeRepository.save(employeeToUpdate);
                ra.addFlashAttribute("success", "Empleado actualizado exitosamente");
            }

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el Empleado: " + e.getMessage());
            if (employeeFromForm.getIdEmployee() == null) {
                return "redirect:/employees/form";
            } else {
                return "redirect:/employees/edit/" + employeeFromForm.getIdEmployee();
            }
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

    @PostMapping("/toggle-active/{id}")
    @Transactional
    public String toggleActiveStatus(@PathVariable("id") Long idEmployee, RedirectAttributes ra) {
        try {
            Employee employee = employeeRepository.findById(idEmployee).orElse(null);
            if (employee == null) {
                ra.addFlashAttribute("error", "Empleado no encontrado");
                return "redirect:/employees";
            }

            User user = employee.getUser();
            if (user == null) {
                ra.addFlashAttribute("error", "El empleado no tiene una cuenta de usuario asociada.");
                return "redirect:/employees";
            }
            boolean newState = !employee.isActive();
            employee.setActive(newState);
            user.setActive(newState);

            employeeRepository.save(employee);
            userRepository.save(user);

            String statusMessage = newState ? "reactivado" : "inactivado";
            ra.addFlashAttribute("success", "Empleado " + employee.getFirstName() + " ha sido " + statusMessage + ".");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al cambiar el estado del empleado.");
        }
        return "redirect:/employees";
    }

    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewEmployeeDetails(@PathVariable("id") Long id) {
        try {
            Employee employee = employeeRepository.findById(id).orElse(null);
            if (employee == null) {
                return ResponseEntity.notFound().build();
            }

            Hibernate.initialize(employee.getUser());

            Map<String, Object> response = new HashMap<>();
            response.put("idEmployee", employee.getIdEmployee());
            response.put("firstName", employee.getFirstName());
            response.put("lastName", employee.getLastName());
            response.put("document", employee.getDocument());
            response.put("phone", employee.getPhone());
            response.put("address", employee.getAddress());
            response.put("active", employee.isActive());

            if (employee.getUser() != null) {
                response.put("email", employee.getUser().getEmail());
                if (employee.getUser().getRole() != null) {
                    response.put("roleName", employee.getUser().getRole().getName());
                } else {
                    response.put("roleName", "Sin Rol Asignado");
                }
            } else {
                response.put("email", "Sin cuenta de usuario");
                response.put("roleName", "N/A");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
