package com.crm.controller;
import com.crm.entity.Client;
import com.crm.entity.User;
import com.crm.service.ClientService;
import com.crm.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.List;
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
    public String list(Model m, @RequestParam(required=false) String search, @AuthenticationPrincipal UserDetails ud) {
        User cu = getUser(ud);
        List<Client> clients = (search != null && !search.isEmpty()) ? clientService.search(search) :
            (cu != null && cu.getRole() == User.Role.USER) ? clientService.findByUser(cu) : clientService.findAll();
        m.addAttribute("clients", clients);
        m.addAttribute("search", search);
        m.addAttribute("user", cu);
        m.addAttribute("statuses", Client.Status.values());
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
    public String add(@Valid @ModelAttribute Client client, BindingResult r, Model m, RedirectAttributes ra) {
        if (r.hasErrors()) { m.addAttribute("users", userService.findAll()); m.addAttribute("statuses", Client.Status.values()); return "client/form"; }
        clientService.save(client);
        ra.addFlashAttribute("success", "Client added!");
        return "redirect:/clients";
    }
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model m, @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("client", clientService.findById(id).orElseThrow());
        m.addAttribute("users", userService.findAll());
        m.addAttribute("statuses", Client.Status.values());
        m.addAttribute("user", getUser(ud));
        return "client/form";
    }
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute Client client, BindingResult r, Model m, RedirectAttributes ra) {
        if (r.hasErrors()) { m.addAttribute("users", userService.findAll()); m.addAttribute("statuses", Client.Status.values()); return "client/form"; }
        client.setId(id); clientService.save(client);
        ra.addFlashAttribute("success", "Client updated!");
        return "redirect:/clients";
    }
    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model m, @AuthenticationPrincipal UserDetails ud) {
        m.addAttribute("client", clientService.findById(id).orElseThrow());
        m.addAttribute("user", getUser(ud));
        return "client/view";
    }
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        clientService.delete(id); ra.addFlashAttribute("success", "Client deleted."); return "redirect:/clients";
    }
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=clients.csv");
        response.getWriter().write(clientService.exportToCsv());
    }
    @PostMapping("/import")
    public String importCsv(@RequestParam("file") MultipartFile file, RedirectAttributes ra) {
        try { ra.addFlashAttribute("success", clientService.importFromCsv(file) + " clients imported!"); }
        catch (Exception e) { ra.addFlashAttribute("error", "Import failed: " + e.getMessage()); }
        return "redirect:/clients";
    }
}
