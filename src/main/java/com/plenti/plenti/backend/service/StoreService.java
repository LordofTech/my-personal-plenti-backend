package com.plenti.plenti.backend.service;

import com.plenti.plenti.backend.dto.StoreDTO;
import com.plenti.plenti.backend.entity.Store;
import com.plenti.plenti.backend.repository.StoreRepository;
import com.plenti.plenti.backend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class StoreService {
    
    @Autowired
    private StoreRepository storeRepository;
    
    public List<StoreDTO> findAll() {
        return StreamSupport.stream(storeRepository.findAll().spliterator(), false)
                .map(Mapper::toStoreDTO)
                .collect(Collectors.toList());
    }
    
    public StoreDTO save(StoreDTO storeDTO) {
        Store store = Mapper.toStore(storeDTO);
        return Mapper.toStoreDTO(storeRepository.save(store));
    }
    
    public StoreDTO findById(String idStr) {
        Optional<Store> optionalStore = storeRepository.findById(idStr);  // ✅ FIXED
        return optionalStore.map(Mapper::toStoreDTO)
                .orElseThrow(() -> new RuntimeException("Store not found"));
    }
    
    public StoreDTO update(String idStr, StoreDTO storeDTO) {
        Optional<Store> optionalStore = storeRepository.findById(idStr);  // ✅ FIXED
        if (optionalStore.isEmpty()) {
            throw new RuntimeException("Store not found");
        }
        
        Store existingStore = optionalStore.get();
        existingStore.setName(storeDTO.getName());
        existingStore.setLocation(storeDTO.getLocation());
        existingStore.setType(storeDTO.getType());
        existingStore.setInventoryCapacity(storeDTO.getInventoryCapacity());
        
        return Mapper.toStoreDTO(storeRepository.save(existingStore));
    }
    
    public String assignStore(String userIdStr) {
        Optional<Store> nearestStore = StreamSupport.stream(storeRepository.findAll().spliterator(), false)
                .findFirst();
        
        return nearestStore.map(store -> String.valueOf(store.getId()))
                .orElseThrow(() -> new RuntimeException("No stores available"));
    }
}