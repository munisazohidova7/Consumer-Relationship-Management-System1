package com.crm.component;

import com.github.javafaker.Faker;
import com.crm.entity.Client;
import com.crm.entity.Order;
import com.crm.repository.ClientRepository;
import com.crm.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;

    @Override
    public void run(String... args) throws Exception {
        
        if (clientRepository.count() == 0) {
            
            Faker faker = new Faker(new Locale("en-US"));
            Random random = new Random();
            
            System.out.println(">>> AWS MySQL uchun ma'lumotlar bazasini to'ldirish boshlandi...");

            String[] statuses = {"ACTIVE", "LEAD", "PENDING"};

            for (int i = 0; i < 35; i++) {
                String firstName = faker.name().firstName();
                String lastName = faker.name().lastName();
                
                Client client = new Client();
                client.setName(firstName + " " + lastName);
                client.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + faker.internet().domainName());
                client.setPhone(faker.phoneNumber().cellPhone());
                client.setStatus(statuses[random.nextInt(statuses.length)]);
                
                Client savedClient = clientRepository.save(client);
                
                int orderCount = random.nextInt(3) + 1; 
                for (int j = 0; j < orderCount; j++) {
                    Order order = new Order();
                    order.setClient(savedClient);
                    order.setAmount(random.nextDouble() * 800 + 50);
                    order.setOrderDate(LocalDate.now().minusDays(random.nextInt(30)));
                    order.setStatus("COMPLETED");
                    
                    orderRepository.save(order);
                }
            }

            System.out.println(">>> Dashboard uchun 35 ta mijoz va ularning buyurtmalari muvaffaqiyatli yuklandi! 🔥");
        }
    }
}
