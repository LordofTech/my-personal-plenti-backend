package com.plenti.plentibackend.util;

import com.plenti.plentibackend.dto.*;
import com.plenti.plentibackend.entity.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Utility class for mapping between entities and DTOs
 */
@Component
public class Mapper {

    public UserDTO toUserDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setReferralCode(user.getReferralCode());
        dto.setMetaCoins(user.getMetaCoins());
        dto.setPaymentMethods(user.getPaymentMethods());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public User toUserEntity(UserDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(dto.getPassword());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setReferralCode(dto.getReferralCode());
        user.setMetaCoins(dto.getMetaCoins());
        user.setPaymentMethods(dto.getPaymentMethods());
        return user;
    }

    public ProductDTO toProductDTO(Product product) {
        if (product == null) return null;
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());
        dto.setLastUpdated(product.getLastUpdated());
        dto.setCategoryId(product.getCategoryId());
        dto.setAverageRating(product.getAverageRating());
        dto.setReviewCount(product.getReviewCount());
        return dto;
    }

    public Product toProductEntity(ProductDTO dto) {
        if (dto == null) return null;
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
        product.setCategoryId(dto.getCategoryId());
        return product;
    }

    public CategoryDTO toCategoryDTO(Category category) {
        if (category == null) return null;
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());
        dto.setParentId(category.getParentId());
        if (category.getSubcategories() != null) {
            dto.setSubcategories(category.getSubcategories().stream()
                    .map(this::toCategoryDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public Category toCategoryEntity(CategoryDTO dto) {
        if (dto == null) return null;
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());
        category.setParentId(dto.getParentId());
        return category;
    }

    public CartDTO toCartDTO(Cart cart) {
        if (cart == null) return null;
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setItems(cart.getItems());
        dto.setTotal(cart.getTotal());
        dto.setLastUpdated(cart.getLastUpdated());
        return dto;
    }

    public WishlistDTO toWishlistDTO(Wishlist wishlist) {
        if (wishlist == null) return null;
        WishlistDTO dto = new WishlistDTO();
        dto.setId(wishlist.getId());
        dto.setUserId(wishlist.getUserId());
        dto.setProductIds(wishlist.getProductIds());
        dto.setCreatedAt(wishlist.getCreatedAt());
        return dto;
    }

    public OrderDTO toOrderDTO(Order order) {
        if (order == null) return null;
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setProductIds(order.getProductIds());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setTrackingUrl(order.getTrackingUrl());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setRiderId(order.getRiderId());
        dto.setRiderName(order.getRiderName());
        dto.setEstimatedDelivery(order.getEstimatedDelivery());
        dto.setPromoCodeApplied(order.getPromoCodeApplied());
        dto.setDiscountAmount(order.getDiscountAmount());
        return dto;
    }

    public Order toOrderEntity(OrderDTO dto) {
        if (dto == null) return null;
        Order order = new Order();
        order.setId(dto.getId());
        order.setUserId(dto.getUserId());
        order.setProductIds(dto.getProductIds());
        order.setTotalAmount(dto.getTotalAmount());
        order.setStatus(dto.getStatus());
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setPromoCodeApplied(dto.getPromoCodeApplied());
        order.setDiscountAmount(dto.getDiscountAmount());
        return order;
    }

    public PaymentDTO toPaymentDTO(Payment payment) {
        if (payment == null) return null;
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrderId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setReference(payment.getReference());
        dto.setMethod(payment.getMethod());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }

    public StoreDTO toStoreDTO(Store store) {
        if (store == null) return null;
        StoreDTO dto = new StoreDTO();
        dto.setId(store.getId());
        dto.setName(store.getName());
        dto.setLocation(store.getLocation());
        dto.setType(store.getType());
        dto.setInventoryCapacity(store.getInventoryCapacity());
        dto.setLatitude(store.getLatitude());
        dto.setLongitude(store.getLongitude());
        return dto;
    }

    public Store toStoreEntity(StoreDTO dto) {
        if (dto == null) return null;
        Store store = new Store();
        store.setId(dto.getId());
        store.setName(dto.getName());
        store.setLocation(dto.getLocation());
        store.setType(dto.getType());
        store.setInventoryCapacity(dto.getInventoryCapacity());
        store.setLatitude(dto.getLatitude());
        store.setLongitude(dto.getLongitude());
        return store;
    }

    public AddressDTO toAddressDTO(Address address) {
        if (address == null) return null;
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setUserId(address.getUserId());
        dto.setLabel(address.getLabel());
        dto.setStreetAddress(address.getStreetAddress());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setCountry(address.getCountry());
        dto.setPostalCode(address.getPostalCode());
        dto.setLatitude(address.getLatitude());
        dto.setLongitude(address.getLongitude());
        dto.setIsDefault(address.getIsDefault());
        return dto;
    }

    public Address toAddressEntity(AddressDTO dto) {
        if (dto == null) return null;
        Address address = new Address();
        address.setId(dto.getId());
        address.setUserId(dto.getUserId());
        address.setLabel(dto.getLabel());
        address.setStreetAddress(dto.getStreetAddress());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setPostalCode(dto.getPostalCode());
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());
        address.setIsDefault(dto.getIsDefault());
        return address;
    }

    public ReviewDTO toReviewDTO(Review review) {
        if (review == null) return null;
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUserId());
        dto.setProductId(review.getProductId());
        dto.setOrderId(review.getOrderId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }

    public Review toReviewEntity(ReviewDTO dto) {
        if (dto == null) return null;
        Review review = new Review();
        review.setId(dto.getId());
        review.setUserId(dto.getUserId());
        review.setProductId(dto.getProductId());
        review.setOrderId(dto.getOrderId());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return review;
    }

    public PromoCodeDTO toPromoCodeDTO(PromoCode promoCode) {
        if (promoCode == null) return null;
        PromoCodeDTO dto = new PromoCodeDTO();
        dto.setId(promoCode.getId());
        dto.setCode(promoCode.getCode());
        dto.setDiscountType(promoCode.getDiscountType());
        dto.setDiscountValue(promoCode.getDiscountValue());
        dto.setMinOrderAmount(promoCode.getMinOrderAmount());
        dto.setMaxUses(promoCode.getMaxUses());
        dto.setUsedCount(promoCode.getUsedCount());
        dto.setValidFrom(promoCode.getValidFrom());
        dto.setValidTo(promoCode.getValidTo());
        dto.setActive(promoCode.getActive());
        return dto;
    }

    public PromoCode toPromoCodeEntity(PromoCodeDTO dto) {
        if (dto == null) return null;
        PromoCode promoCode = new PromoCode();
        promoCode.setId(dto.getId());
        promoCode.setCode(dto.getCode());
        promoCode.setDiscountType(dto.getDiscountType());
        promoCode.setDiscountValue(dto.getDiscountValue());
        promoCode.setMinOrderAmount(dto.getMinOrderAmount());
        promoCode.setMaxUses(dto.getMaxUses());
        promoCode.setUsedCount(dto.getUsedCount());
        promoCode.setValidFrom(dto.getValidFrom());
        promoCode.setValidTo(dto.getValidTo());
        promoCode.setActive(dto.getActive());
        return promoCode;
    }
}
