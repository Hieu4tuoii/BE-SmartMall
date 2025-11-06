package vn.hieu4tuoi.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import vn.hieu4tuoi.service.CartService;
import vn.hieu4tuoi.repository.CartItemRepository;
import vn.hieu4tuoi.Security.SecurityUtils;
import vn.hieu4tuoi.dto.request.cart.UpdateCartItemRequest;
import vn.hieu4tuoi.exception.BadRequestException;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.exception.UnauthorizedException;
import vn.hieu4tuoi.repository.ProductColorVersionRepository;
import vn.hieu4tuoi.model.CartItem;
import vn.hieu4tuoi.model.ProductColorVersion;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductColorVersionRepository productColorVersionRepository;
    // @Override
    // public String addToCart( String productColorVersionId, Integer quantity) {

    // }

    @Override
    public String updateCartItem(UpdateCartItemRequest request) {
        String productColorVersionId = request.getProductColorVersionId();
        Integer quantity = request.getQuantity();
        // lấy userId từ SecurityContext
        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng");
        }

        // kiểm tra product color version
        ProductColorVersion productColorVersion = productColorVersionRepository
                .findByIdAndIsDeleted(productColorVersionId, false);
        if (productColorVersion == null) {
            throw new ResourceNotFoundException("Màu sắc sản phẩm không tồn tại");
        }

        // kiểm tra đã có trong giỏ hàng chưa, nếu có thì update cộng thêm số lượng
        CartItem cartItemExist = cartItemRepository.findByUserIdAndProductColorVersionIdAndIsDeleted(userId,
                productColorVersionId, false);
        if (cartItemExist != null) {
            quantity += cartItemExist.getQuantity();
        }

        // kiểm tra số lượng tồn kho
        if (productColorVersion.getTotalStock() < quantity) {
            throw new BadRequestException("Màu sắc sản phẩm đã hết hàng");
        }

        // lưu cart item

        CartItem cartItem = new CartItem();
        if(cartItemExist != null) {
            cartItem = cartItemExist;
        }
        cartItem.setUserId(userId);
        cartItem.setProductColorVersionId(productColorVersionId);
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return cartItem.getId();
    }

    @Override
    public Integer getCartItemCount() {
        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để lấy số lượng sản phẩm trong giỏ hàng");
        }
        return cartItemRepository.countByUserIdAndIsDeleted(userId, false);
    }
}
