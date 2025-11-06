package vn.hieu4tuoi.service;

import vn.hieu4tuoi.dto.request.cart.UpdateCartItemRequest;
import vn.hieu4tuoi.dto.respone.cart.CartResponse;

public interface CartService {
    // String addToCart( String productColorVersionId, Integer quantity);
    String updateCartItem( UpdateCartItemRequest request);
    Integer getCartItemCount();
    CartResponse getCartDetail();
}
