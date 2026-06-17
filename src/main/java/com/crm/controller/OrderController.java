package com.crm.controller;
import com.crm.entity.Order;
import com.crm.entity.User;
import com.crm.service.ClientService;
import com.crm.service.OrderService;
import com.crm.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final ClientService clientService;
    private final UserService userService;

    private User getUser(UserDetails ud) {
        return userService.findByEmail(ud.getUsername()).orElse(null);
    }

    @GetMapping
    public String list(Model m,
                       @RequestParam(required=false) String status,
                       @RequestParam(required=false) String search,
                       @RequestParam(defaultValue="0") int page,
                       @RequestParam(defaultValue="10") int size,
                       @AuthenticationPrincipal UserDetails ud) {
        User cu = getUser(ud);
        Page<Order> orderPage;

        if (status != null && !status.isEmpty()) {
            orderPage = orderService.findByStatusPaged(Order.Status.valueOf(status), page, size);
        } else if (search != null && !search.isEmpty()) {
            orderPage = orderService.searchPaged(search, page, size);
        } else {
            orderPage = orderService.findAllPaged(page, size);
        }

        m.addAttribute("orders", orderPage.getContent());
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", orderPage.getTotalPages());
        m.addAttribute("totalElements", orderPage.getTotalElements());
        m.addAttribute("size", size);
        m.addAttribute("search", search);
        m.addAttribute("selectedStatus", status);
        m.addAttribute("statuses", Order.Status.values());
        m.addAttribute("user", cu);
        return "order/list";
    }

    @GetMapping("/add")
    public String addForm(Model m, @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("order", new Order());
        m.addAttribute("clients", clientService.findAll());
        m.addAttribute("statuses", Order.Status.values());
        m.addAttribute("user", getUser(ud));
        return "order/form";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute Order order,
                      BindingResult r,
                      @RequestParam(value="clientId", required=false) Long clientId,
                      Model m, RedirectAttributes ra) {
        if (r.hasErrors()) {
            m.addAttribute("clients", clientService.findAll());
            m.addAttribute("statuses", Order.Status.values());
            return "order/form";
        }
        if (clientId != null) {
            clientService.findById(clientId).ifPresent(order::setClient);
        }
        orderService.save(order);
        ra.addFlashAttribute("success", "Order added successfully!");
        return "redirect:/orders";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model m,
                           @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("order", orderService.findById(id).orElseThrow());
        m.addAttribute("clients", clientService.findAll());
        m.addAttribute("statuses", Order.Status.values());
        m.addAttribute("user", getUser(ud));
        return "order/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute Order order,
                       BindingResult r,
                       @RequestParam(value="clientId", required=false) Long clientId,
                       Model m, RedirectAttributes ra) {
        if (r.hasErrors()) {
            m.addAttribute("clients", clientService.findAll());
            m.addAttribute("statuses", Order.Status.values());
            return "order/form";
        }
        order.setId(id);
        if (clientId != null) {
            clientService.findById(clientId).ifPresent(order::setClient);
        }
        orderService.save(order);
        ra.addFlashAttribute("success", "Order updated!");
        return "redirect:/orders";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        orderService.delete(id);
        ra.addFlashAttribute("success", "Order deleted.");
        return "redirect:/orders";
    }
}
