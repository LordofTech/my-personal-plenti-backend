package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.ProductDTO;
import com.plenti.plentibackend.entity.Product;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.ProductRepository;
import com.plenti.plentibackend.util.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(50);

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product");
        productDTO.setPrice(100.0);
        productDTO.setStock(50);
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));
        when(mapper.toProductDTO(product)).thenReturn(productDTO);

        List<ProductDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mapper.toProductDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_WhenProductNotExists_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PlentiException.class, () -> productService.getProductById(1L));
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        when(mapper.toProductEntity(productDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(mapper.toProductDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.createProduct(productDTO);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void restockProduct_ShouldIncreaseStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.restockProduct(1L, 20);

        assertEquals(70, product.getStock());
        verify(productRepository, times(1)).save(product);
    }
}
