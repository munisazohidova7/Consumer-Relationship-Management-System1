package com.crm.component;

import com.github.javafaker.Faker;
import com.crm.entity.Client;
import com.crm.entity.Order;
import com.crm.repository.ClientRepository;
import com.crm.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;

    @Override
    public void run(String... args) throws Exception {
        
        // Agar bazada mijozlar umuman yo'q bo'lsa, soxta ma'lumot yuklaymiz
        if (clientRepository.count() == 0) {
            
            Faker faker = new Faker(new Locale("en-US"));
            Random random = new Random();
            
            System.out.println(">>> AWS MySQL uchun ma'lumotlar bazasini to'ldirish boshlandi...");

            Client.Status[] clientStatuses = Client.Status.values();
            Order.Status[] orderStatuses = Order.Status.values();
            String[] productTypes = {"SaaS License", "Cloud Storage", "Consulting", "Support Pack"};

            for (int i = 0; i < 35; i++) {
                // 1. Mijoz (Client) ob'ektini real maydonlar bilan to'ldirish
                Client client = new Client();
                client.setCompanyName(faker.company().name());
                client.setContactPerson(faker.name().fullName());
                client.setEmail(faker.internet().emailAddress(client.getContactPerson().toLowerCase().replace(" ", ".")));
                client.setPhone(faker.phoneNumber().cellPhone());
                client.setAddress(faker.address().streetAddress());
                client.setCity(faker.address().city());
                client.setCountry(faker.address().country());
                client.setStatus(clientStatuses[random.nextInt(clientStatuses.length)]);
                client.setNotes("Faker tomonidan avtomatik yaratilgan mijoz.");
                
                // Mijozni saqlaymiz
                Client savedClient = clientRepository.save(client);
                
                // 2. Har bir mijoz uchun 1 tadan 3 tagacha buyurtma yaratish
                int orderCount = random.nextInt(3) + 1; 
                for (int j = 0; j < orderCount; j++) {
                    Order order = new Order();
                    
                    // Unikal buyurtma raqami generatsiyasi (masalan: ORD-123456)
                    order.setOrderNumber("ORD-" + faker.number().numberBetween(100000, 999999));
                    order.setClient(savedClient);
                    
                    // Pul miqdorini BigDecimal ko'rinishida sozlash
                    double randomAmount = random.nextDouble() * 1500 + 100; // $100 dan $1600 gacha
                    order.setTotalAmount(BigDecimal.valueOf(randomAmount).setScale(2, BigDecimal.ROUND_HALF_UP));
                    
                    order.setStatus(orderStatuses[random.nextInt(orderStatuses.length)]);
                    order.setProductType(productTypes[random.nextInt(productTypes.length)]);
                    order.setQuantity(random.nextInt(5) + 1);
                    order.setDescription(order.getProductType() + " x" + order.getQuantity() + " uchun shartnoma.");
                    
                    orderRepository.save(order);
                }
            }

            System.out.println(">>> Dashboard uchun 35 ta mijoz va ularning buyurtmalari muvaffaqiyatli yuklandi! 🔥");
        }
    }
}
