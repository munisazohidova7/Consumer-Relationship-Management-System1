package com.crm.controller;
import com.crm.entity.Client;
import com.crm.entity.User;
import com.crm.service.ClientService;
import com.crm.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;

@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;
    private final UserService userService;

    private User getUser(UserDetails ud) {
        return userService.findByEmail(ud.getUsername()).orElse(null);
    }

    @GetMapping
    public String list(Model m,
                       @RequestParam(required=false) String search,
                       @RequestParam(required=false) String status,
                       @RequestParam(defaultValue="0") int page,
                       @RequestParam(defaultValue="10") int size,
                       @AuthenticationPrincipal UserDetails ud) {
        User cu = getUser(ud);
        Page<Client> clientPage;

        if (search != null && !search.isEmpty()) {
            clientPage = clientService.searchPaged(search, page, size);
        } else if (status != null && !status.isEmpty()) {
            clientPage = clientService.findByStatusPaged(Client.Status.valueOf(status), page, size);
        } else if (cu != null && cu.getRole() == User.Role.USER) {
            clientPage = clientService.findByUserPaged(cu, page, size);
        } else {
            clientPage = clientService.findAllPaged(page, size);
        }

        m.addAttribute("clients", clientPage.getContent());
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", clientPage.getTotalPages());
        m.addAttribute("totalElements", clientPage.getTotalElements());
        m.addAttribute("size", size);
        m.addAttribute("search", search);
        m.addAttribute("selectedStatus", status);
        m.addAttribute("statuses", Client.Status.values());
        m.addAttribute("user", cu);
        return "client/list";
    }

    @GetMapping("/add")
    public String addForm(Model m, @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("client", new Client());
        m.addAttribute("users", userService.findAll());
        m.addAttribute("statuses", Client.Status.values());
        m.addAttribute("user", getUser(ud));
        return "client/form";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute Client client,
                      BindingResult r,
                      @RequestParam(value="assignedToId", required=false) Long assignedToId,
                      Model m, RedirectAttributes ra) {
        if (r.hasErrors()) {
            m.addAttribute("users", userService.findAll());
            m.addAttribute("statuses", Client.Status.values());
            return "client/form";
        }
        if (assignedToId != null) {
            userService.findById(assignedToId).ifPresent(client::setAssignedTo);
        } else {
            client.setAssignedTo(null);
        }
        clientService.save(client);
        ra.addFlashAttribute("success", "Client added successfully!");
        return "redirect:/clients";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model m,
                           @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("client", clientService.findById(id).orElseThrow());
        m.addAttribute("users", userService.findAll());
        m.addAttribute("statuses", Client.Status.values());
        m.addAttribute("user", getUser(ud));
        return "client/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute Client client,
                       BindingResult r,
                       @RequestParam(value="assignedToId", required=false) Long assignedToId,
                       Model m, RedirectAttributes ra) {
        if (r.hasErrors()) {
            m.addAttribute("users", userService.findAll());
            m.addAttribute("statuses", Client.Status.values());
            return "client/form";
        }
        client.setId(id);
        if (assignedToId != null) {
            userService.findById(assignedToId).ifPresent(client::setAssignedTo);
        } else {
            client.setAssignedTo(null);
        }
        clientService.save(client);
        ra.addFlashAttribute("success", "Client updated!");
        return "redirect:/clients";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model m,
                       @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("client", clientService.findById(id).orElseThrow());
        m.addAttribute("user", getUser(ud));
        return "client/view";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        clientService.delete(id);
        ra.addFlashAttribute("success", "Client deleted.");
        return "redirect:/clients";
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=clients.csv");
        response.getWriter().write(clientService.exportToCsv());
    }

    @PostMapping("/import")
    public String importCsv(@RequestParam("file") MultipartFile file, RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("success", clientService.importFromCsv(file) + " clients imported!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Import failed: " + e.getMessage());
        }
        return "redirect:/clients";
    }
}
