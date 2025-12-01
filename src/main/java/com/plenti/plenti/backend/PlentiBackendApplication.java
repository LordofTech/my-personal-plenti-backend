package com.plenti.plenti.backend;

import com.plenti.plenti.backend.entity.Product;
import com.plenti.plenti.backend.entity.Store;
import com.plenti.plenti.backend.entity.User;
import com.plenti.plenti.backend.repository.ProductRepository;  // Add import for ProductRepository
import com.plenti.plenti.backend.repository.StoreRepository;    // Add import for StoreRepository
import com.plenti.plenti.backend.repository.jpa.UserRepository;  // Fixed: Use .jpa for UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.plenti.plenti.backend.repository")  // Fixed: Match your repository package
public class PlentiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlentiBackendApplication.class, args);
    }
}

@Component
class DataLoader {  // Made non-static for smoother autowiring

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void loadData() {
        // Sample stores (dark stores in Lagos LGAs)
        Store store1 = new Store();
        store1.setName("Ikeja Dark Store");
        store1.setLocation("Ikeja, Lagos");
        store1.setType("dark");
        store1.setInventoryCapacity(10000);
        storeRepository.save(store1);

        Store store2 = new Store();
        store2.setName("Surulere Partner Store");
        store2.setLocation("Surulere, Lagos");
        store2.setType("partner");
        store2.setInventoryCapacity(5000);
        storeRepository.save(store2);

        // Sample products (from screenshots, real Nigerian FMCG, prices in Naira updated for 2025)
        Product product1 = new Product();
        product1.setName("Golden Penny Pasta Spaghetti Big");
        product1.setDescription("500g pack, premium pasta");
        product1.setPrice(1200.0);  // Updated based on 2025 listings (around ₦1,000-1,300)
        product1.setCategory("Food");
        product1.setStock(100);
        product1.setImageUrl("https://example.com/golden-penny.jpg");
        product1.setLastUpdated(LocalDateTime.of(2025, 12, 1, 12, 0));  // Updated to current date
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Snickers Chocolate 60g");
        product2.setDescription("Chocolate bar");
        product2.setPrice(600.0);  // Updated based on 2025 listings (around ₦500-700)
        product2.setCategory("Food");
        product2.setStock(50);
        product2.setImageUrl("https://example.com/snickers.jpg");
        product2.setLastUpdated(LocalDateTime.of(2025, 12, 1, 9, 30));  // Updated to current date
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("Declan De Espana Non-Alcoholic Wine");
        product3.setDescription("750ml bottle");
        product3.setPrice(9500.0);  // Updated based on 2025 listings (around ₦9,000-10,000)
        product3.setCategory("Beverages");
        product3.setStock(30);
        product3.setImageUrl("https://example.com/wine.jpg");
        product3.setLastUpdated(LocalDateTime.of(2025, 12, 1, 14, 45));  // Updated to current date
        productRepository.save(product3);

        // Sample user (from profile screenshot, DOB 2000, referral code)
        User user = new User();
        // Let ID auto-generate; don't set manually unless needed
        user.setName("Chinedu Opara");
        user.setEmail("chinedu.opara@gmail.com");
        user.setPhoneNumber("08012345678");  // Use phoneNumber
        user.setDateOfBirth(Date.from(LocalDate.of(2000, 4, 15).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        user.setPassword("hashed_password");  // Use proper hashing in production
        user.setReferralCode("ABC123");
        user.setMetaCoins(0.0);
        // Save via repository
        userRepository.save(user);
    }
}
