package vn.hieu4tuoi.service;

import vn.hieu4tuoi.dto.request.cart.UpdateCartItemRequest;

public interface CartService {
    // String addToCart( String productColorVersionId, Integer quantity);
    String updateCartItem( UpdateCartItemRequest request);
    Integer getCartItemCount();
}
