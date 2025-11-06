package vn.hieu4tuoi.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.catalina.security.SecurityUtil;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.hieu4tuoi.service.OrderService;
import vn.hieu4tuoi.Security.SecurityUtils;
import vn.hieu4tuoi.dto.request.order.OrderRequest;
import vn.hieu4tuoi.exception.BadRequestException;
import vn.hieu4tuoi.exception.UnauthorizedException;

import org.springframework.transaction.annotation.Transactional;
import vn.hieu4tuoi.repository.OrderRepository;
import vn.hieu4tuoi.mapper.OrderMapper;
import vn.hieu4tuoi.model.CartItem;
import vn.hieu4tuoi.model.Order;
import vn.hieu4tuoi.model.OrderItem;
import vn.hieu4tuoi.model.ProductColorVersion;
import vn.hieu4tuoi.model.ProductVersion;
import vn.hieu4tuoi.model.Promotion;
import vn.hieu4tuoi.repository.CartItemRepository;
import vn.hieu4tuoi.repository.OrderItemRepository;
import vn.hieu4tuoi.repository.ProductColorVersionRepository;
import vn.hieu4tuoi.repository.ProductVersionRepository;
import vn.hieu4tuoi.repository.PromotionRepository;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductColorVersionRepository productColorVersionRepository;
    private final ProductVersionRepository productVersionRepository;
    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    //**đang còn lỗi
    public String createOrder(OrderRequest request) {

        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để tạo đơn hàng");
        }

        Order order = orderMapper.requestToEntity(request);
        order.setUserId(userId);
        orderRepository.save(order);

        // get cart items và chuyển dần sang orderItem
        List<CartItem> cartItems = cartItemRepository.findByUserIdAndIsDeletedOrderByCreatedAtDesc(userId,
                false);

        // ds product color version
        List<String> productColorVersionIds = cartItems.stream()
                .map(CartItem::getProductColorVersionId)
                .toList();
        List<ProductColorVersion> productColorVersions = productColorVersionRepository
                .findAllByIdInAndIsDeleted(productColorVersionIds, false);
        Map<String, ProductColorVersion> productColorVersionMap = productColorVersions.stream()
                .collect(Collectors.toMap(ProductColorVersion::getId, Function.identity()));

        // ds product version
        List<String> productVersionIds = productColorVersions.stream()
                .map(ProductColorVersion::getProductVersionId)
                .toList();
        List<ProductVersion> productVersions = productVersionRepository.findAllByIdInAndIsDeleted(productVersionIds,
                false);
        Map<String, ProductVersion> productVersionMap = productVersions.stream()
                .collect(Collectors.toMap(ProductVersion::getId, Function.identity()));

        // ds promotion
        List<String> promotionIds = productVersions.stream()
                .map(ProductVersion::getPromotionId)
                .toList();
        List<Promotion> promotions = promotionRepository
                .findAllByIdInAndStartAtLessThanEqualAndEndAtGreaterThanEqual(promotionIds, LocalDateTime.now(), false);
        Map<String, Promotion> promotionMap = promotions.stream()
                .collect(Collectors.toMap(Promotion::getId, Function.identity()));

        // với mỗi quantity trong cart item, tạo mới một order item
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            ProductColorVersion productColorVersion = productColorVersionMap.get(cartItem.getProductColorVersionId());
            //kiểm tra quantity có lớn hơn số lượng sản phẩm trong kho không
            if (cartItem.getQuantity() > productColorVersion.getTotalStock().intValue()) {
                throw new BadRequestException("Số lượng sản phẩm trong kho không đủ");
            }
            ProductVersion productVersion = productVersionMap.get(productColorVersion.getProductVersionId());
            Promotion promotion = promotionMap.get(productVersion.getPromotionId());
            long discountedPrice = productVersion.getPrice();
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
                // Làm tròn đến nghìn đồng
                discountedPrice = (long) Math.round((productVersion.getPrice() - discountAmount) / 1000.0) * 1000;
            }

            for (int i = 0; i < cartItem.getQuantity(); i++) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                // orderItem.setProductItemId(cartItem.getProductItemId());
                orderItem.setPrice(productVersion.getPrice());
                orderItem.setDiscountedPrice(discountedPrice);
                orderItem.setProductVersionId(productVersion.getId());
                orderItems.add(orderItem);
            }

            //update lại số lượng sản phẩm trong kho và tổng số lượng đã bán
            productColorVersion.setTotalStock(productColorVersion.getTotalStock() - cartItem.getQuantity());
            productColorVersion.setTotalSold(productColorVersion.getTotalSold() + cartItem.getQuantity());
            productVersion.setTotalSold(productVersion.getTotalSold() + cartItem.getQuantity());
            productVersionRepository.save(productVersion);
            productColorVersionRepository.save(productColorVersion);
        }

        //clear cart items
        for (CartItem cartItem : cartItems) {
            cartItem.setIsDeleted(true);
        }
        cartItemRepository.saveAll(cartItems);
        orderItemRepository.saveAll(orderItems);
        return order.getId();
    }
}
