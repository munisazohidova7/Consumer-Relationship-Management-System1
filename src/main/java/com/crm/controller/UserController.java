package com.crm.controller;
import com.crm.entity.User;
import com.crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private User getUser(UserDetails ud) {
        return userService.findByEmail(ud.getUsername()).orElse(null);
    }

    @GetMapping
    public String list(Model m, @AuthenticationPrincipal UserDetails ud) {
        List<User> users = userService.findAll();
        m.addAttribute("users", users);
        m.addAttribute("adminCount", users.stream().filter(u -> u.getRole() == User.Role.ADMIN).count());
        m.addAttribute("managerCount", users.stream().filter(u -> u.getRole() == User.Role.MANAGER).count());
        m.addAttribute("userCount", users.stream().filter(u -> u.getRole() == User.Role.USER).count());
        m.addAttribute("user", getUser(ud));
        return "user/list";
    }

    @GetMapping("/add")
    public String addForm(Model m, @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("userForm", new User());
        m.addAttribute("roles", User.Role.values());
        m.addAttribute("user", getUser(ud));
        return "user/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("userForm") User userForm,
                      RedirectAttributes ra) {
        userForm.setPassword(passwordEncoder.encode(userForm.getPassword()));
        userService.save(userForm);
        ra.addFlashAttribute("success", "User added successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model m,
                           @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("userForm", userService.findById(id).orElseThrow());
        m.addAttribute("roles", User.Role.values());
        m.addAttribute("user", getUser(ud));
        return "user/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @ModelAttribute("userForm") User userForm,
                       RedirectAttributes ra) {
        User existing = userService.findById(id).orElseThrow();
        existing.setFirstName(userForm.getFirstName());
        existing.setLastName(userForm.getLastName());
        existing.setEmail(userForm.getEmail());
        existing.setRole(userForm.getRole());
        if (userForm.getPassword() != null && !userForm.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(userForm.getPassword()));
        }
        userService.save(existing);
        ra.addFlashAttribute("success", "User updated!");
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        userService.delete(id);
        ra.addFlashAttribute("success", "User deleted.");
        return "redirect:/admin/users";
    }
}
