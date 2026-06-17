package com.crm.service;
import com.crm.entity.Client;
import com.crm.entity.User;
import com.crm.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public List<Client> findAll() {
        return clientRepository.findAll(Sort.by("createdAt").descending());
    }

    public Page<Client> findAllPaged(int page, int size) {
        return clientRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Page<Client> searchPaged(String search, int page, int size) {
        return clientRepository.findByCompanyNameContainingIgnoreCaseOrContactPersonContainingIgnoreCase(
            search, search, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Page<Client> findByStatusPaged(Client.Status status, int page, int size) {
        return clientRepository.findByStatus(status, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Page<Client> findByUserPaged(User user, int page, int size) {
        return clientRepository.findByAssignedTo(user, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public List<Client> findByUser(User user) {
        return clientRepository.findByAssignedTo(user);
    }

    public List<Client> search(String query) {
        return clientRepository.findByCompanyNameContainingIgnoreCaseOrContactPersonContainingIgnoreCase(query, query);
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public void delete(Long id) {
        clientRepository.deleteById(id);
    }

    public long countAll() {
        return clientRepository.count();
    }

    public long countByStatus(Client.Status status) {
        return clientRepository.countByStatus(status);
    }

    public List<Client> findRecent(int limit) {
        return clientRepository.findAll(PageRequest.of(0, limit, Sort.by("createdAt").descending())).getContent();
    }

    public String exportToCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Company Name,Contact Person,Email,Phone,City,Country,Status\n");
        for (Client c : findAll()) {
            sb.append(c.getId()).append(",")
              .append(c.getCompanyName()).append(",")
              .append(c.getContactPerson()).append(",")
              .append(c.getEmail() != null ? c.getEmail() : "").append(",")
              .append(c.getPhone() != null ? c.getPhone() : "").append(",")
              .append(c.getCity() != null ? c.getCity() : "").append(",")
              .append(c.getCountry() != null ? c.getCountry() : "").append(",")
              .append(c.getStatus()).append("\n");
        }
        return sb.toString();
    }

    public int importFromCsv(MultipartFile file) throws Exception {
        int count = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        br.readLine();
        while ((line = br.readLine()) != null) {
            String[] fields = line.split(",");
            if (fields.length >= 4) {
                Client c = new Client();
                c.setCompanyName(fields[0].trim());
                c.setContactPerson(fields[1].trim());
                c.setEmail(fields[2].trim());
                c.setPhone(fields[3].trim());
                if (fields.length > 4) c.setCity(fields[4].trim());
                if (fields.length > 5) c.setCountry(fields[5].trim());
                c.setStatus(Client.Status.LEAD);
                clientRepository.save(c);
                count++;
            }
        }
        return count;
    }
}
