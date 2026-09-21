package com.dionicio.marktplatz.service;

import com.dionicio.marktplatz.dto.ProductRequest;
import com.dionicio.marktplatz.dto.ProductResponse;
import com.dionicio.marktplatz.entity.Category;
import com.dionicio.marktplatz.entity.Product;
import com.dionicio.marktplatz.repository.CategoryRepository;
import com.dionicio.marktplatz.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @CacheEvict("products")
    public ProductResponse createProduct(ProductRequest productRequest) {
        Long categoryId = productRequest.categoryId();
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) {
            throw new RuntimeException("No category found");
        }
        Category category = optionalCategory.get();

        Product product = new Product();
        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setPrice(productRequest.price());
        product.setStockQuantity(productRequest.stockQuantity());
        product.setCategory(category);
        product.setImageUrl(productRequest.imageUrl());
        product.setCreatedAt(LocalDateTime.now());

        productRepository.save(product);

        return toResponse(product);
    }

    public ProductResponse getProductById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        Product product = optionalProduct.get();

        return toResponse(product);
    }

    @CacheEvict(value = "products", allEntries = true)
    public Page<ProductResponse> listOfProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::toResponse);
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        Product product = optionalProduct.get();

        Optional<Category> optionalCategory = categoryRepository.findById(productRequest.categoryId());
        if (optionalCategory.isEmpty()) {
            throw new RuntimeException("No category found");
        }
        Category category = optionalCategory.get();

        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setPrice(productRequest.price());
        product.setStockQuantity(productRequest.stockQuantity());
        product.setCategory(category);
        product.setImageUrl(productRequest.imageUrl());

        productRepository.save(product);

        return toResponse(product);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory().getName(),
                product.getImageUrl()
        );
    }
}
