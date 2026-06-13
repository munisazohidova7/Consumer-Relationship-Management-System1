package com.crm.controller;
import com.crm.entity.User;
import com.crm.service.ClientService;
import com.crm.service.OrderService;
import com.crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final ClientService clientService;
    private final OrderService orderService;
    private final UserService userService;
    @GetMapping("/")
    public String home() { return "redirect:/dashboard"; }
    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("totalClients", clientService.countAll());
        model.addAttribute("activeClients", clientService.countByStatus(com.crm.entity.Client.Status.ACTIVE));
        model.addAttribute("leadClients", clientService.countByStatus(com.crm.entity.Client.Status.LEAD));
        model.addAttribute("totalOrders", orderService.countAll());
        model.addAttribute("pendingOrders", orderService.countByStatus(com.crm.entity.Order.Status.PENDING));
        model.addAttribute("monthlyOrders", orderService.countThisMonth());
        model.addAttribute("totalRevenue", orderService.getTotalRevenue());
        model.addAttribute("totalUsers", userService.countUsers());
        User currentUser = userService.findByEmail(userDetails.getUsername()).orElse(null);
        model.addAttribute("user", currentUser);
        return "dashboard/index";
    }
    @GetMapping("/login")
    public String login() { return "auth/login"; }
    @GetMapping("/access-denied")
    public String accessDenied() { return "auth/access-denied"; }
}
