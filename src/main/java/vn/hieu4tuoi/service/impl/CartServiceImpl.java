package vn.hieu4tuoi.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import vn.hieu4tuoi.service.CartService;
import vn.hieu4tuoi.repository.CartItemRepository;
import vn.hieu4tuoi.Security.SecurityUtils;
import vn.hieu4tuoi.dto.request.cart.UpdateCartItemRequest;
import vn.hieu4tuoi.dto.respone.cart.CartItemResponse;
import vn.hieu4tuoi.dto.respone.cart.CartResponse;
import vn.hieu4tuoi.exception.BadRequestException;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.exception.UnauthorizedException;
import vn.hieu4tuoi.repository.ProductColorVersionRepository;
import vn.hieu4tuoi.model.CartItem;
import vn.hieu4tuoi.model.Image;
import vn.hieu4tuoi.model.Product;
import vn.hieu4tuoi.model.ProductColorVersion;
import vn.hieu4tuoi.model.ProductVersion;
import vn.hieu4tuoi.model.Promotion;
import vn.hieu4tuoi.repository.ProductRepository;
import vn.hieu4tuoi.repository.ProductVersionRepository;
import vn.hieu4tuoi.repository.PromotionRepository;
import vn.hieu4tuoi.repository.ImageRepository;
import vn.hieu4tuoi.service.ProductService;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductColorVersionRepository productColorVersionRepository;
    private final ProductVersionRepository productVersionRepository;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final PromotionRepository promotionRepository;
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
        if (cartItemExist != null) {
            cartItem = cartItemExist;
            // nếu số lượng là 0 thì xóa cart item
            if (quantity <= 0) {
                cartItem.setIsDeleted(true);
            }
        } else {
            //nếu số lượng <=0 thì throw exception
            if (quantity <= 0) {
                throw new BadRequestException("Số lượng sản phẩm phải lớn hơn 0");
            }
            cartItem.setUserId(userId);
            cartItem.setProductColorVersionId(productColorVersionId);
        }
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

    @Override
    public CartResponse getCartDetail() {
        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để lấy thông tin giỏ hàng");
        }

        // list cart item
        List<CartItem> cartItems = cartItemRepository.findByUserIdAndIsDeletedOrderByCreatedAtDesc(userId, false);

        // lấy thông tin product version color
        List<String> productColorVersionIds = cartItems.stream()
                .map(CartItem::getProductColorVersionId)
                .toList();
        List<ProductColorVersion> productColorVersions = productColorVersionRepository
                .findAllByIdInAndIsDeleted(productColorVersionIds, false);
        Map<String, ProductColorVersion> productColorVersionMap = productColorVersions.stream()
                .collect(Collectors.toMap(ProductColorVersion::getId, p -> p));

        // lấy thông tin product version
        List<String> productVersionIds = productColorVersions.stream()
                .map(ProductColorVersion::getProductVersionId)
                .toList();
        List<ProductVersion> productVersions = productVersionRepository.findAllByIdInAndIsDeleted(productVersionIds,
                false);
        Map<String, ProductVersion> productVersionMap = productVersions.stream()
                .collect(Collectors.toMap(ProductVersion::getId, p -> p));

        // lấy thông tin product
        List<String> productIds = productVersions.stream()
                .map(ProductVersion::getProductId)
                .toList();
        List<Product> products = productRepository.findAllByIdInAndIsDeleted(productIds, false);
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // lấy thông tin image
        List<Image> imageList = imageRepository.findAllByIsDefaultAndProductIdInAndIsDeleted(true, productIds, false);
        Map<String, String> imageMap = imageList.stream()
                .collect(Collectors.toMap(Image::getProductId, Image::getUrl));

        // lấy thông tin khuyến mãi
        List<String> promotionIds = productVersions.stream()
                .map(ProductVersion::getPromotionId)
                .toList();
        List<Promotion> promotions = promotionRepository
                .findAllByIdInAndStartAtLessThanEqualAndEndAtGreaterThanEqual(promotionIds, LocalDateTime.now(), false);
        Map<String, Promotion> promotionMap = promotions.stream()
                .collect(Collectors.toMap(Promotion::getId, p -> p));

        // build cart response
        CartResponse response = new CartResponse();
        List<CartItemResponse> cartItemResponses = cartItems.stream().map(item -> {
            CartItemResponse cartItemResponse = new CartItemResponse();
            cartItemResponse.setId(item.getId());
            cartItemResponse.setProductColorVersionId(item.getProductColorVersionId());
            // color
            ProductColorVersion productColorVersion = productColorVersionMap.get(item.getProductColorVersionId());
            cartItemResponse.setColorName(productColorVersion.getColor());
            // version
            ProductVersion productVersion = productVersionMap.get(productColorVersion.getProductVersionId());
            cartItemResponse.setProductVersionName(productVersion.getName());
            // product
            Product product = productMap.get(productVersion.getProductId());
            cartItemResponse.setProductName(product.getName());
            // image
            String imageUrl = imageMap.get(product.getId());
            cartItemResponse.setImageUrl(imageUrl);
            // quantity
            cartItemResponse.setQuantity(item.getQuantity());
            // total price
            cartItemResponse.setTotalPrice(productVersion.getPrice() * item.getQuantity());

            // promotion
            Promotion promotion = promotionMap.get(productVersion.getPromotionId());
            if (promotion != null) {
                // % discount
                double discountPercent = promotion.getDiscount();
                // giá dc giảm
                double discountAmount = productVersion.getPrice() * discountPercent / 100;

                // double discountedPrice = productVersion.getPrice() - discountAmount;
                // nếu lớn hơn max discount thì cần set lại và tính lại % discount
                if (discountAmount > promotion.getMaximumDiscountAmount()) {
                    discountAmount = promotion.getMaximumDiscountAmount();
                    discountPercent = discountAmount * 100 / productVersion.getPrice();
                }
                // response.setDiscount(Math.round(discountPercent));
                // Làm tròn đến nghìn đồng
                long discountedPrice = (long) Math.round((productVersion.getPrice() - discountAmount) / 1000.0) * 1000;
                // response.setDiscountedPrice(discountedPrice);
                cartItemResponse.setTotalPrice(discountedPrice * item.getQuantity());
            }
            cartItemResponse.setSlug(productVersion.getSlug());
            return cartItemResponse;
        }).toList();
        response.setCartItems(cartItemResponses);
        response.setTotalItem(cartItemResponses.size());
        response.setTotalPrice(cartItemResponses.stream().mapToLong(CartItemResponse::getTotalPrice).sum());

        return response;
    }
}
