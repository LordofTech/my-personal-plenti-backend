package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.AddressDTO;
import com.plenti.plentibackend.entity.Address;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.AddressRepository;
import com.plenti.plentibackend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for address management operations
 */
@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private Mapper mapper;

    public List<AddressDTO> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(mapper::toAddressDTO)
                .toList();
    }

    public AddressDTO getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new PlentiException("Address not found"));
        return mapper.toAddressDTO(address);
    }

    @Transactional
    public AddressDTO createAddress(AddressDTO addressDTO) {
        // If this is set as default, unset other defaults for this user
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault()) {
            unsetDefaultAddresses(addressDTO.getUserId());
        }

        Address address = mapper.toAddressEntity(addressDTO);
        Address savedAddress = addressRepository.save(address);
        return mapper.toAddressDTO(savedAddress);
    }

    @Transactional
    public AddressDTO updateAddress(Long id, AddressDTO addressDTO) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new PlentiException("Address not found"));

        if (addressDTO.getLabel() != null) address.setLabel(addressDTO.getLabel());
        if (addressDTO.getStreetAddress() != null) address.setStreetAddress(addressDTO.getStreetAddress());
        if (addressDTO.getCity() != null) address.setCity(addressDTO.getCity());
        if (addressDTO.getState() != null) address.setState(addressDTO.getState());
        if (addressDTO.getCountry() != null) address.setCountry(addressDTO.getCountry());
        if (addressDTO.getPostalCode() != null) address.setPostalCode(addressDTO.getPostalCode());
        if (addressDTO.getLatitude() != null) address.setLatitude(addressDTO.getLatitude());
        if (addressDTO.getLongitude() != null) address.setLongitude(addressDTO.getLongitude());

        Address updatedAddress = addressRepository.save(address);
        return mapper.toAddressDTO(updatedAddress);
    }

    @Transactional
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new PlentiException("Address not found");
        }
        addressRepository.deleteById(id);
    }

    @Transactional
    public AddressDTO setDefaultAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new PlentiException("Address not found"));

        unsetDefaultAddresses(address.getUserId());
        address.setIsDefault(true);
        Address updatedAddress = addressRepository.save(address);
        return mapper.toAddressDTO(updatedAddress);
    }

    private void unsetDefaultAddresses(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        addresses.forEach(addr -> {
            addr.setIsDefault(false);
            addressRepository.save(addr);
        });
    }
}
