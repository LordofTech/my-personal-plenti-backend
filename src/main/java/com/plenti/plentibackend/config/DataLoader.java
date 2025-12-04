package com.plenti.plentibackend.config;

import com.plenti.plentibackend.entity.Category;
import com.plenti.plentibackend.entity.Product;
import com.plenti.plentibackend.entity.Role;
import com.plenti.plentibackend.entity.Store;
import com.plenti.plentibackend.entity.User;
import com.plenti.plentibackend.repository.CategoryRepository;
import com.plenti.plentibackend.repository.ProductRepository;
import com.plenti.plentibackend.repository.RoleRepository;
import com.plenti.plentibackend.repository.StoreRepository;
import com.plenti.plentibackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Data loader to seed sample data on application startup
 */
@Component
@Order(2) // Run after AppInitializer
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Load sample data only if database is empty
        if (userRepository.count() == 0) {
            loadSampleData();
        }
    }

    private void loadSampleData() {
        // Get default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        // Create sample user
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+2348012345678");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setReferralCode("PLTJOHN123");
        user.setMetaCoins(0.0);
        user.setEnabled(true); // Sample user is pre-verified
        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        // Create sample categories
        Category groceries = new Category();
        groceries.setName("Groceries");
        groceries.setDescription("Essential food items and household goods");
        groceries.setImageUrl("https://example.com/groceries.jpg");
        categoryRepository.save(groceries);

        Category beverages = new Category();
        beverages.setName("Beverages");
        beverages.setDescription("Drinks and refreshments");
        beverages.setImageUrl("https://example.com/beverages.jpg");
        categoryRepository.save(beverages);

        Category snacks = new Category();
        snacks.setName("Snacks");
        snacks.setDescription("Quick bites and treats");
        snacks.setImageUrl("https://example.com/snacks.jpg");
        categoryRepository.save(snacks);

        // Create sample products
        Product product1 = new Product();
        product1.setName("Indomie Instant Noodles");
        product1.setDescription("Popular instant noodles - Pack of 5");
        product1.setPrice(500.0);
        product1.setCategory("Groceries");
        product1.setStock(100);
        product1.setImageUrl("https://example.com/indomie.jpg");
        product1.setCategoryId(groceries.getId());
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Peak Milk Powder");
        product2.setDescription("Premium milk powder - 400g");
        product2.setPrice(1200.0);
        product2.setCategory("Groceries");
        product2.setStock(50);
        product2.setImageUrl("https://example.com/peak-milk.jpg");
        product2.setCategoryId(groceries.getId());
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("Coca-Cola 50cl");
        product3.setDescription("Refreshing soft drink");
        product3.setPrice(200.0);
        product3.setCategory("Beverages");
        product3.setStock(200);
        product3.setImageUrl("https://example.com/coca-cola.jpg");
        product3.setCategoryId(beverages.getId());
        productRepository.save(product3);

        // Create sample stores
        Store store1 = new Store();
        store1.setName("Ikeja Dark Store");
        store1.setLocation("Ikeja, Lagos");
        store1.setType("dark");
        store1.setInventoryCapacity(1000);
        store1.setLatitude(6.5964);
        store1.setLongitude(3.3486);
        storeRepository.save(store1);

        Store store2 = new Store();
        store2.setName("Surulere Partner Store");
        store2.setLocation("Surulere, Lagos");
        store2.setType("partner");
        store2.setInventoryCapacity(500);
        store2.setLatitude(6.4969);
        store2.setLongitude(3.3612);
        storeRepository.save(store2);

        System.out.println("Sample data loaded successfully!");
    }
}
