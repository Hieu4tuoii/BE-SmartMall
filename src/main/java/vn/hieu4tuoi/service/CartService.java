package vn.hieu4tuoi.service;

public interface CartService {
    String addToCart( String productColorVersionId, Integer quantity);
    void updateCartItem( String productColorVersionId, Integer quantity);
}
