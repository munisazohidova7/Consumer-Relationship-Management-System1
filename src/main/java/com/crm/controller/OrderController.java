package com.crm.controller;

import com.crm.entity.Order;
import com.crm.entity.User;
import com.crm.service.ClientService;
import com.crm.service.OrderService;
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
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ClientService clientService;
    private final UserService userService;

    @GetMapping
    public String list(Model m, @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("orders", orderService.findAll());
        m.addAttribute("user", userService.findByEmail(ud.getUsername()).orElse(null));
        return "order/list";
    }

    @GetMapping("/add")
    public String addForm(Model m, @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("order", new Order());
        m.addAttribute("clients", clientService.findAll());
        m.addAttribute("statuses", Order.Status.values());
        m.addAttribute("user", userService.findByEmail(ud.getUsername()).orElse(null));
        return "order/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Order order,
                      @AuthenticationPrincipal UserDetails ud,
                      RedirectAttributes ra) {
        order.setCreatedBy(userService.findByEmail(ud.getUsername()).orElse(null));
        orderService.save(order);
        ra.addFlashAttribute("success", "Order created!");
        return "redirect:/orders";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model m,
                           @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("order", orderService.findById(id).orElseThrow());
        m.addAttribute("clients", clientService.findAll());
        m.addAttribute("statuses", Order.Status.values());
        m.addAttribute("user", userService.findByEmail(ud.getUsername()).orElse(null));
        return "order/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @ModelAttribute Order order,
                       RedirectAttributes ra) {
        order.setId(id);
        orderService.save(order);
        ra.addFlashAttribute("success", "Order updated!");
        return "redirect:/orders";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        orderService.delete(id);
        ra.addFlashAttribute("success", "Order deleted.");
        return "redirect:/orders";
    }
}
