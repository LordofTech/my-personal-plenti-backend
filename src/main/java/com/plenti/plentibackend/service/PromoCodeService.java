package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.PromoCodeDTO;
import com.plenti.plentibackend.entity.PromoCode;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.PromoCodeRepository;
import com.plenti.plentibackend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for promo code operations
 */
@Service
public class PromoCodeService {

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    @Autowired
    private Mapper mapper;

    public List<PromoCodeDTO> getAllPromoCodes() {
        return promoCodeRepository.findAll().stream()
                .map(mapper::toPromoCodeDTO)
                .toList();
    }

    @Transactional
    public PromoCodeDTO createPromoCode(PromoCodeDTO promoCodeDTO) {
        if (promoCodeRepository.findByCode(promoCodeDTO.getCode()).isPresent()) {
            throw new PlentiException("Promo code already exists");
        }

        PromoCode promoCode = mapper.toPromoCodeEntity(promoCodeDTO);
        PromoCode savedPromoCode = promoCodeRepository.save(promoCode);
        return mapper.toPromoCodeDTO(savedPromoCode);
    }

    public PromoCodeDTO validatePromoCode(String code, Double orderAmount) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new PlentiException("Invalid promo code"));

        if (!promoCode.getActive()) {
            throw new PlentiException("Promo code is not active");
        }

        LocalDateTime now = LocalDateTime.now();
        if (promoCode.getValidFrom() != null && now.isBefore(promoCode.getValidFrom())) {
            throw new PlentiException("Promo code is not yet valid");
        }

        if (promoCode.getValidTo() != null && now.isAfter(promoCode.getValidTo())) {
            throw new PlentiException("Promo code has expired");
        }

        if (promoCode.getUsedCount() >= promoCode.getMaxUses()) {
            throw new PlentiException("Promo code usage limit reached");
        }

        if (orderAmount < promoCode.getMinOrderAmount()) {
            throw new PlentiException("Order amount does not meet minimum requirement");
        }

        return mapper.toPromoCodeDTO(promoCode);
    }

    @Transactional
    public Double applyPromoCode(String code, Double orderAmount) {
        PromoCodeDTO promoCodeDTO = validatePromoCode(code, orderAmount);
        
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new PlentiException("Promo code not found"));

        promoCode.setUsedCount(promoCode.getUsedCount() + 1);
        promoCodeRepository.save(promoCode);

        if ("PERCENTAGE".equals(promoCode.getDiscountType())) {
            return orderAmount * (promoCode.getDiscountValue() / 100);
        } else {
            return promoCode.getDiscountValue();
        }
    }
}
