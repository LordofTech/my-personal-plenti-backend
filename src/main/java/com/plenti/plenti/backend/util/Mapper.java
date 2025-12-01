package com.plenti.plenti.backend.util;

import com.plenti.plenti.backend.dto.*;
import com.plenti.plenti.backend.entity.*;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Mapper {
    private Mapper() {} // Utility class

    public static User toUser(UserDTO dto) {
        User user = new User();
        user.setId(Long.parseLong(dto.getId()));
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getDateOfBirth() != null) {
            user.setDateOfBirth(Date.from(dto.getDateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        user.setPassword(dto.getPassword());
        user.setReferralCode(dto.getReferralCode());
        user.setMetaCoins(dto.getMetaCoins());
        Map<String, String> paymentMap = new HashMap<>();
        for (Map<String, String> map : dto.getPaymentMethods()) {
            paymentMap.putAll(map);
        }
        user.setPaymentMethods(paymentMap);
        return user;
    }

    public static UserDTO toUserDTO(User entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId().toString());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        if (entity.getDateOfBirth() != null) {
            dto.setDateOfBirth(entity.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        dto.setPassword(entity.getPassword());
        dto.setReferralCode(entity.getReferralCode());
        dto.setMetaCoins(entity.getMetaCoins());
        List<Map<String, String>> paymentList = new ArrayList<>();
        if (!entity.getPaymentMethods().isEmpty()) {
            paymentList.add(new HashMap<>(entity.getPaymentMethods()));
        }
        dto.setPaymentMethods(paymentList);
        return dto;
    }

    public static Product toProduct(ProductDTO dto) {
        Product product = new Product();
        product.setId(Long.parseLong(dto.getId()));
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
        product.setLastUpdated(dto.getLastUpdated());
        return product;
    }

    public static ProductDTO toProductDTO(Product entity) {
        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId().toString());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setCategory(entity.getCategory());
        dto.setStock(entity.getStock());
        dto.setImageUrl(entity.getImageUrl());
        dto.setLastUpdated(entity.getLastUpdated());
        return dto;
    }

    public static Order toOrder(OrderDTO dto) {
        Order order = new Order();
        order.setId(Long.parseLong(dto.getId()));
        order.setUserId(Long.parseLong(dto.getUserId()));
        List<String> productIds = new ArrayList<>();
        for (String id : dto.getProductIds()) {
            productIds.add(id);
        }
        order.setProductIds(productIds);
        order.setTotalAmount(dto.getTotalAmount());
        order.setStatus(dto.getStatus());
        order.setOrderDate(dto.getOrderDate());
        order.setTrackingUrl(dto.getTrackingUrl());
        return order;
    }

    public static OrderDTO toOrderDTO(Order entity) {
        OrderDTO dto = new OrderDTO();
        dto.setId(entity.getId().toString());
        dto.setUserId(entity.getUserId().toString());
        List<String> productIdsDto = new ArrayList<>();
        for (String id : entity.getProductIds()) {
            productIdsDto.add(id);
        }
        dto.setProductIds(productIdsDto);
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setStatus(entity.getStatus());
        dto.setOrderDate(entity.getOrderDate());
        dto.setTrackingUrl(entity.getTrackingUrl());
        return dto;
    }

    public static Cart toCart(CartDTO dto) {
        Cart cart = new Cart();
        cart.setId(Long.parseLong(dto.getId()));
        cart.setUserId(Long.parseLong(dto.getUserId()));
        Map<Long, Integer> items = new HashMap<>();
        for (Map.Entry<String, Integer> entry : dto.getItems().entrySet()) {
            items.put(Long.parseLong(entry.getKey()), entry.getValue());
        }
        cart.setItems(items);
        cart.setTotal(dto.getTotal());
        return cart;
    }

    public static CartDTO toCartDTO(Cart entity) {
        CartDTO dto = new CartDTO();
        dto.setId(entity.getId().toString());
        dto.setUserId(entity.getUserId().toString());
        Map<String, Integer> itemsDto = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : entity.getItems().entrySet()) {
            itemsDto.put(entry.getKey().toString(), entry.getValue());
        }
        dto.setItems(itemsDto);
        dto.setTotal(entity.getTotal());
        return dto;
    }

    public static Payment toPayment(PaymentDTO dto) {
        Payment payment = new Payment();
        payment.setId(Long.parseLong(dto.getId()));
        payment.setOrderId(Long.parseLong(dto.getOrderId()));
        payment.setAmount(dto.getAmount());
        payment.setStatus(dto.getStatus());
        payment.setReference(dto.getReference());
        return payment;
    }

    public static PaymentDTO toPaymentDTO(Payment entity) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(entity.getId().toString());
        dto.setOrderId(entity.getOrderId().toString());
        dto.setAmount(entity.getAmount());
        dto.setStatus(entity.getStatus());
        dto.setReference(entity.getReference());
        return dto;
    }

    public static Store toStore(StoreDTO dto) {
        Store store = new Store();
        store.setId(Long.parseLong(dto.getId()));
        store.setName(dto.getName());
        store.setLocation(dto.getLocation());
        store.setType(dto.getType());
        store.setInventoryCapacity(dto.getInventoryCapacity());
        return store;
    }

    public static StoreDTO toStoreDTO(Store entity) {
        StoreDTO dto = new StoreDTO();
        dto.setId(entity.getId().toString());
        dto.setName(entity.getName());
        dto.setLocation(entity.getLocation());
        dto.setType(entity.getType());
        dto.setInventoryCapacity(entity.getInventoryCapacity());
        return dto;
    }
}