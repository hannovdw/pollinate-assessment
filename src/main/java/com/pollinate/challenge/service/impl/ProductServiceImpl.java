package com.pollinate.challenge.service.impl;

import com.pollinate.challenge.controller.dto.ProductRequest;
import com.pollinate.challenge.controller.dto.ProductResponse;
import com.pollinate.challenge.domain.Product;
import com.pollinate.challenge.exception.ResourceNotFoundException;
import com.pollinate.challenge.repository.ProductRepository;
import com.pollinate.challenge.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;

  @Override
  @Transactional
  public ProductResponse createProduct(ProductRequest request) {
    log.info("Creating new product: {}", request.getName());

    Product product = Product.builder()
        .name(request.getName())
        .price(request.getPrice())
        .build();

    Product savedProduct = productRepository.save(product);
    return mapToResponse(savedProduct);
  }

  @Override
  @Transactional(readOnly = true)
  public ProductResponse getProductById(Long id) {
    return productRepository.findById(id)
        .map(this::mapToResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ProductResponse> getAllProducts() {
    return productRepository.findAll().stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  private ProductResponse mapToResponse(Product product) {
    return ProductResponse.builder()
        .id(product.getId())
        .name(product.getName())
        .price(product.getPrice())
        .build();
  }
}