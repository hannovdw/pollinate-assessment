package com.pollinate.challenge.service;

import com.pollinate.challenge.controller.dto.ProductRequest;
import com.pollinate.challenge.controller.dto.ProductResponse;
import com.pollinate.challenge.domain.Product;
import com.pollinate.challenge.exception.ResourceNotFoundException;
import com.pollinate.challenge.repository.ProductRepository;
import com.pollinate.challenge.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductServiceImpl productService;

  @Test
  @DisplayName("Should successfully create a product from a valid request")
  void createProduct_Success() {
    // Arrange
    ProductRequest request = new ProductRequest("Laptop", new BigDecimal("999.99"));
    Product savedProduct = Product.builder().id(1L).name("Laptop").price(new BigDecimal("999.99")).build();

    // Mock the repository save behavior
    when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

    // Act
    ProductResponse response = productService.createProduct(request);

    // Assert
    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals("Laptop", response.getName());
    verify(productRepository, times(1)).save(any(Product.class));
  }

  @Test
  @DisplayName("Should find product by ID and map correctly to Response DTO")
  void getProductById_Success() {
    // Arrange
    Product foundProduct = Product.builder().id(2L).name("Mouse").price(BigDecimal.TEN).build();

    // Mock the repository findById behavior
    when(productRepository.findById(2L)).thenReturn(Optional.of(foundProduct));

    // Act
    ProductResponse response = productService.getProductById(2L);

    // Assert
    assertNotNull(response);
    assertEquals(2L, response.getId());
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when product ID is invalid")
  void getProductById_NotFound() {
    // Arrange
    // Mock the repository to return an empty optional (not found)
    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
  }

  @Test
  @DisplayName("Should return a list of all products")
  void getAllProducts_Success() {
    // Arrange
    List<Product> productList = List.of(
        Product.builder().id(1L).name("A").price(BigDecimal.ONE).build(),
        Product.builder().id(2L).name("B").price(BigDecimal.TEN).build()
    );

    when(productRepository.findAll()).thenReturn(productList);

    // Act
    List<ProductResponse> responses = productService.getAllProducts();

    // Assert
    assertNotNull(responses);
    assertEquals(2, responses.size());
    assertEquals("A", responses.get(0).getName());
    verify(productRepository, times(1)).findAll();
  }
}
