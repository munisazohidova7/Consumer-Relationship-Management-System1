package com.crm.service;
import com.crm.entity.Client;
import com.crm.entity.User;
import com.crm.repository.ClientRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    public Client save(Client client) { return clientRepository.save(client); }
    public Optional<Client> findById(Long id) { return clientRepository.findById(id); }
    public List<Client> findAll() { return clientRepository.findAll(); }
    public List<Client> findByUser(User user) { return clientRepository.findByAssignedTo(user); }
    public List<Client> search(String query) { return clientRepository.findByCompanyNameContainingIgnoreCase(query); }
    public void delete(Long id) { clientRepository.deleteById(id); }
    public long countByStatus(Client.Status status) { return clientRepository.countByStatus(status); }
    public long countAll() { return clientRepository.count(); }
    public String exportToCsv() throws IOException {
        List<Client> clients = clientRepository.findAll();
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);
        writer.writeNext(new String[]{"ID","Company","Contact","Email","Phone","City","Country","Status"});
        for (Client c : clients) {
            writer.writeNext(new String[]{
                String.valueOf(c.getId()), c.getCompanyName(), c.getContactPerson(),
                c.getEmail(), c.getPhone(), c.getCity(), c.getCountry(),
                c.getStatus() != null ? c.getStatus().name() : ""
            });
        }
        writer.close();
        return sw.toString();
    }
    public int importFromCsv(MultipartFile file) throws Exception {
        int count = 0;
        CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
        List<String[]> rows = reader.readAll();
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length < 3) continue;
            clientRepository.save(Client.builder()
                .companyName(row[0]).contactPerson(row[1]).email(row[2])
                .phone(row.length > 3 ? row[3] : "")
                .city(row.length > 4 ? row[4] : "")
                .country(row.length > 5 ? row[5] : "")
                .status(Client.Status.LEAD).build());
            count++;
        }
        reader.close();
        return count;
    }
}
