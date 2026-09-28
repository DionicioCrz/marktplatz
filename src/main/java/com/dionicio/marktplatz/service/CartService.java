package com.dionicio.marktplatz.service;

import com.dionicio.marktplatz.dto.CartItemResponse;
import com.dionicio.marktplatz.dto.CartResponse;
import com.dionicio.marktplatz.entity.Cart;
import com.dionicio.marktplatz.entity.CartItem;
import com.dionicio.marktplatz.entity.Product;
import com.dionicio.marktplatz.entity.User;
import com.dionicio.marktplatz.repository.CartItemRepository;
import com.dionicio.marktplatz.repository.CartRepository;
import com.dionicio.marktplatz.repository.ProductRepository;
import com.dionicio.marktplatz.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Cart getOrCreateCart(String userId) {
        Long userIdLong = Long.valueOf(userId);
        Optional<Cart> cartOptional = cartRepository.findByUserId(userIdLong);

        if (cartOptional.isPresent()) {
            return cartOptional.get();
        }

        Optional<User> userOptional = userRepository.findById(userIdLong);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        Cart newCart = new Cart();

        newCart.setUser(user);
        newCart.setCreatedAt(LocalDateTime.now());

        return cartRepository.save(newCart);
    }

    public void addItemToCart(String userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        Product product = productOptional.get();

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            if (newQuantity > product.getStockQuantity()) {
                throw new RuntimeException("Not enough stock");
            }

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            if (quantity > product.getStockQuantity()) {
                throw new RuntimeException("Not enough stock");
            }

            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }

        if (quantity > product.getStockQuantity()) {
            throw new RuntimeException("Not enough stock");
        }
    }

    public void updateCartItemQuantity(String userId, Long cartItemId, Integer newQuantity) {
        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);

        if (cartItemOptional.isEmpty()) {
            throw new RuntimeException("Cart item not found");
        }
        CartItem cartItem = cartItemOptional.get();

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item not found");
        }

        Product product = cartItem.getProduct();
        if (newQuantity > product.getStockQuantity()) {
            throw new RuntimeException("Not enough stock");
        }

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);
    }

    public void removeCartItem(String userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
        if (cartItemOptional.isEmpty()) {
            throw new RuntimeException("Cart item not found");
        }
        CartItem cartItem = cartItemOptional.get();

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item not found");
        }

        cartItemRepository.deleteById(cartItemId);
    }

    public CartResponse getCartResponse(String userId) {
        Cart cart = getOrCreateCart(userId);
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        List<CartItemResponse> itemResponses = cartItems.stream()
                .map(this::toCartItemResponse)
                .toList();

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cart.getId(), itemResponses, total);
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        Product product = item.getProduct();
        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                item.getQuantity(),
                subtotal
        );
    }
}