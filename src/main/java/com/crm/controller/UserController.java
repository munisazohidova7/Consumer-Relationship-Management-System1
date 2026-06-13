package com.crm.controller;
import com.crm.entity.User;
import com.crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping
    public String list(Model m, @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("users", userService.findAll());
        m.addAttribute("user", userService.findByEmail(ud.getUsername()).orElse(null));
        return "user/list";
    }
    @GetMapping("/add")
    public String addForm(Model m, @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("newUser", new User());
        m.addAttribute("roles", User.Role.values());
        m.addAttribute("user", userService.findByEmail(ud.getUsername()).orElse(null));
        return "user/form";
    }
    @PostMapping("/add")
    public String add(@ModelAttribute("newUser") User newUser, RedirectAttributes ra) {
        if (userService.findByEmail(newUser.getEmail()).isPresent()) { ra.addFlashAttribute("error", "Email exists!"); return "redirect:/admin/users/add"; }
        userService.createUser(newUser); ra.addFlashAttribute("success", "User created!"); return "redirect:/admin/users";
    }
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model m, @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("newUser", userService.findById(id).orElseThrow());
        m.addAttribute("roles", User.Role.values());
        m.addAttribute("user", userService.findByEmail(ud.getUsername()).orElse(null));
        return "user/form";
    }
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute("newUser") User u, RedirectAttributes ra) {
        u.setId(id); userService.updateUser(u); ra.addFlashAttribute("success", "User updated!"); return "redirect:/admin/users";
    }
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteUser(id); ra.addFlashAttribute("success", "Deactivated."); return "redirect:/admin/users";
    }
}
