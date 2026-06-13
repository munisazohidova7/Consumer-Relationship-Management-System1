package com.crm.cloudcrm.component;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Locale;
import java.util.Random;

// Loyihangizdagi real paketlardan import qiling:
// import com.crm.cloudcrm.model.Client;
// import com.crm.cloudcrm.repository.ClientRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    // O'zingizning real Repository klasslaringizni shu yerda e'lon qiling:
    // private final ClientRepository clientRepository;

    @Override
    public void run(String... args) throws Exception {
        
        // Baza bo'sh bo'lsagina ma'lumot qo'shish sharti (Takrorlanish bo'lmasligi uchun)
        // if (clientRepository.count() == 0) {
            
            Faker faker = new Faker(new Locale("en-US"));
            Random random = new Random();
            
            System.out.println(">>> AWS MySQL uchun soxta ma'lumotlar generatsiyasi boshlandi...");

            String[] statuses = {"ACTIVE", "LEAD", "PENDING"};

            for (int i = 0; i < 40; i++) {
                String firstName = faker.name().firstName();
                String lastName = faker.name().lastName();
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + faker.internet().domainName();
                String phone = faker.phoneNumber().cellPhone();
                String randomStatus = statuses[random.nextInt(statuses.length)];

                // O'zingizning Client entity ob'ektingizni to'ldiring:
                // Client client = new Client();
                // client.setName(firstName + " " + lastName);
                // client.setEmail(email);
                // client.setPhone(phone);
                // client.setStatus(randomStatus);
                
                // clientRepository.save(client);
            }

            System.out.println(">>> Dashboard uchun 40 ta realistik mijoz ma'lumotlari yuklandi! 🔥");
        // }
    }
}
