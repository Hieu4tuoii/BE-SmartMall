package vn.hieu4tuoi.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.hieu4tuoi.service.OrderService;
import vn.hieu4tuoi.Security.SecurityUtils;
import vn.hieu4tuoi.common.CommonUtils;
import vn.hieu4tuoi.common.StringUtils;
import vn.hieu4tuoi.dto.request.order.OrderRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.order.OrderResponse;
import vn.hieu4tuoi.dto.respone.order.ProductOrderResponse;
import vn.hieu4tuoi.exception.BadRequestException;
import vn.hieu4tuoi.exception.UnauthorizedException;

import org.springframework.transaction.annotation.Transactional;

import vn.hieu4tuoi.repository.OrderRepository;
import vn.hieu4tuoi.mapper.OrderMapper;
import vn.hieu4tuoi.model.CartItem;
import vn.hieu4tuoi.model.Order;
import vn.hieu4tuoi.model.OrderItem;
import vn.hieu4tuoi.model.Product;
import vn.hieu4tuoi.model.ProductColorVersion;
import vn.hieu4tuoi.model.ProductVersion;
import vn.hieu4tuoi.model.Promotion;
import vn.hieu4tuoi.repository.CartItemRepository;
import vn.hieu4tuoi.repository.OrderItemRepository;
import vn.hieu4tuoi.repository.ProductColorVersionRepository;
import vn.hieu4tuoi.repository.ProductVersionRepository;
import vn.hieu4tuoi.repository.PromotionRepository;
import vn.hieu4tuoi.repository.ProductRepository;

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
    private final ProductRepository productRepository;

    @Override
    @Transactional
    // **đang còn lỗi
    public String createOrder(OrderRequest request) {

        String userId = SecurityUtils.getCurrentUserId();
        String userFullName = SecurityUtils.getCurrentUserFullName();
        if (userId == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để tạo đơn hàng");
        }

        Order order = orderMapper.requestToEntity(request);
        order.setUserId(userId);
        // order.setId(UUID.randomUUID().toString());
        order.setFullTextSearch(StringUtils.toFullTextSearch(userFullName + " " + order.getPhoneNumber()));
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
        List<ProductVersion> productVersions = productVersionRepository.findAllByIdInAndIsDeleted(
                productVersionIds,
                false);
        Map<String, ProductVersion> productVersionMap = productVersions.stream()
                .collect(Collectors.toMap(ProductVersion::getId, Function.identity()));

        // ds promotion
        List<String> promotionIds = productVersions.stream()
                .map(ProductVersion::getPromotionId)
                .toList();
        List<Promotion> promotions = promotionRepository
                .findAllByIdInAndStartAtLessThanEqualAndEndAtGreaterThanEqual(promotionIds,
                        LocalDateTime.now(), false);
        Map<String, Promotion> promotionMap = promotions.stream()
                .collect(Collectors.toMap(Promotion::getId, Function.identity()));

        // với mỗi quantity trong cart item, tạo mới một order item
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            ProductColorVersion productColorVersion = productColorVersionMap
                    .get(cartItem.getProductColorVersionId());
            // kiểm tra quantity có lớn hơn số lượng sản phẩm trong kho không
            if (cartItem.getQuantity() > productColorVersion.getTotalStock().intValue()) {
                throw new BadRequestException("Số lượng sản phẩm trong kho không đủ");
            }
            ProductVersion productVersion = productVersionMap
                    .get(productColorVersion.getProductVersionId());
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
                discountedPrice = (long) Math
                        .round((productVersion.getPrice() - discountAmount) / 1000.0) * 1000;
            }

            for (int i = 0; i < cartItem.getQuantity(); i++) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                // orderItem.setProductItemId(cartItem.getProductItemId());
                orderItem.setPrice(productVersion.getPrice());
                orderItem.setDiscountedPrice(discountedPrice);
                orderItem.setProductColorVersionId(productColorVersion.getId());
                orderItems.add(orderItem);
            }

            // update lại số lượng sản phẩm trong kho và tổng số lượng đã bán
            productColorVersion.setTotalStock(productColorVersion.getTotalStock() - cartItem.getQuantity());
            productColorVersion.setTotalSold(productColorVersion.getTotalSold() + cartItem.getQuantity());
            productVersion.setTotalSold(productVersion.getTotalSold() + cartItem.getQuantity());
            productVersionRepository.save(productVersion);
            productColorVersionRepository.save(productColorVersion);
        }

        // clear cart items
        for (CartItem cartItem : cartItems) {
            cartItem.setIsDeleted(true);
        }
        cartItemRepository.saveAll(cartItems);
        orderItemRepository.saveAll(orderItems);
        return order.getId();
    }

    @Override
    public PageResponse<List<OrderResponse>> getOrderList(int page, int size, String sort, String keyword) {
        // Build pageable với sắp xếp
        Pageable pageable = CommonUtils.createPageable(page, size, sort);
        String keywordSearch = CommonUtils.createKeywordSearch(keyword);
        Page<Order> orderPage = orderRepository.findAllByFullTextSearchOrId(keywordSearch, null, pageable);

        //order item
        List<String> orderIds = orderPage.getContent().stream().map(Order::getId).toList();
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdInAndIsDeleted(orderIds, false);
        Map<String, List<OrderItem>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        //ds product color version
        List<String> productColorVersionIds = orderItems.stream()
                .map(OrderItem::getProductColorVersionId)
                .toList();
        List<ProductColorVersion> productColorVersions = productColorVersionRepository.findAllByIdInAndIsDeleted(productColorVersionIds, false);
        Map<String, ProductColorVersion> productColorVersionMap = productColorVersions.stream()
                .collect(Collectors.toMap(ProductColorVersion::getId, Function.identity()));

        //ds product version
        List<String> productVersionIds = productColorVersions.stream()
                .map(ProductColorVersion::getProductVersionId)
                .toList();
        List<ProductVersion> productVersions = productVersionRepository.findAllByIdInAndIsDeleted(productVersionIds, false);
        Map<String, ProductVersion> productVersionMap = productVersions.stream()
                .collect(Collectors.toMap(ProductVersion::getId, Function.identity()));

        //ds product
        List<String> productIds = productVersions.stream()
                .map(ProductVersion::getProductId)
                .toList();
        List<Product> products = productRepository.findAllByIdInAndIsDeleted(productIds, false);
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<OrderResponse> orderResponseList = new ArrayList<>();
        for (Order order : orderPage.getContent()) {
            OrderResponse orderResponse = orderMapper.entityToResponse(order);
            //get list order item của order
            List<OrderItem> orderItemsOrder = orderItemMap.get(order.getId());
            //set thông tin sản phẩm và tính tổng tiền kèm số lượng từng sản phẩm
            // List<ProductOrderResponse> productOrderResponseList = new ArrayList<>();
            // Map<String, Integer> productQuantityMap = new HashMap<>();
            Map<String, ProductOrderResponse> productOrderResponseMap = new HashMap<>();
            long totalPrice = 0;
            for (OrderItem orderItem : orderItemsOrder) {
                ProductColorVersion productColorVersion = productColorVersionMap.get(orderItem.getProductColorVersionId());
                ProductVersion productVersion = productVersionMap.get(productColorVersion.getProductVersionId());
                Product product = productMap.get(productVersion.getProductId());
                ProductOrderResponse productOrderResponse = new ProductOrderResponse();
                productOrderResponse.setId(orderItem.getId());
                productOrderResponse.setProductName(product.getName());
                productOrderResponse.setProductVersionName(productVersion.getName());
                productOrderResponse.setColorName(productColorVersion.getColor());
                //nếu chưa có số lượng của sản phẩm trong productQuantityMap thì set là 1, nếu có thì tăng lên 1
                int quantity = 1;
                if (productOrderResponseMap.containsKey(orderItem.getProductColorVersionId())) {
                    quantity = productOrderResponseMap.get(orderItem.getProductColorVersionId()).getQuantity() + 1;
                }
                productOrderResponse.setQuantity(quantity);
                productOrderResponseMap.put(orderItem.getProductColorVersionId(), productOrderResponse);
                //tính tổng tiền của sản phẩm ( giá sau khuyến mãi * số lượng)
                // productOrderResponse.setTotalPrice(orderItem.getDiscountedPrice() * quantity);
                totalPrice += orderItem.getDiscountedPrice();
                // productOrderResponseList.add(productOrderResponse);
            }
            orderResponse.setProducts(productOrderResponseMap.values().stream().toList());
            orderResponse.setTotalPrice(totalPrice);
            orderResponseList.add(orderResponse);
        }
        return PageResponse.<List<OrderResponse>>builder()
                .pageNo(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalPage(orderPage.getTotalPages())
                .items(orderResponseList)
                .build();
    }
}
