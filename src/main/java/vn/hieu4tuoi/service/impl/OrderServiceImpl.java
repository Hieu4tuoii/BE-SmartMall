package vn.hieu4tuoi.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import vn.hieu4tuoi.service.OrderService;
import vn.hieu4tuoi.Security.SecurityUtils;
import vn.hieu4tuoi.common.CommonUtils;
import vn.hieu4tuoi.common.OrderStatus;
import vn.hieu4tuoi.common.PaymentMethod;
import vn.hieu4tuoi.common.PaymentStatus;
import vn.hieu4tuoi.common.ProductItemStatus;
import vn.hieu4tuoi.common.ReturnRequestType;
import vn.hieu4tuoi.common.StringUtils;
import vn.hieu4tuoi.dto.request.order.OrderByAIRequest;
import vn.hieu4tuoi.dto.request.order.OrderRequest;
import vn.hieu4tuoi.dto.request.order.ProductItemImeiRequest;
import vn.hieu4tuoi.dto.request.order.ReturnRequestRequest;
import vn.hieu4tuoi.dto.request.order.UpdateOrderStatusRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.order.CustomerOrderAdminResponse;
import vn.hieu4tuoi.dto.respone.order.OrderDetailResponse;
import vn.hieu4tuoi.dto.respone.order.OrderResponse;
import vn.hieu4tuoi.dto.respone.order.OrderAdminResponse;
import vn.hieu4tuoi.dto.respone.order.OrderByAIResponse;
import vn.hieu4tuoi.dto.respone.order.ProductOrderDetailResponse;
import vn.hieu4tuoi.dto.respone.order.ProductOrderResponse;
import vn.hieu4tuoi.dto.respone.order.ProductOrderAdminResponse;
import vn.hieu4tuoi.dto.respone.order.WarrantyClaimResponse;
import vn.hieu4tuoi.dto.respone.order.ReturnRequestResponse;
import vn.hieu4tuoi.dto.respone.order.ProductWarrantyResponse;
import vn.hieu4tuoi.dto.respone.order.ProductReturnResponse;
import vn.hieu4tuoi.exception.BadRequestException;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.exception.UnauthorizedException;

import org.springframework.transaction.annotation.Transactional;

import vn.hieu4tuoi.repository.OrderRepository;
import vn.hieu4tuoi.mapper.OrderMapper;
import vn.hieu4tuoi.model.CartItem;
import vn.hieu4tuoi.model.Image;
import vn.hieu4tuoi.model.Order;
import vn.hieu4tuoi.model.OrderItem;
import vn.hieu4tuoi.model.Product;
import vn.hieu4tuoi.model.ProductColorVersion;
import vn.hieu4tuoi.model.ProductItem;
import vn.hieu4tuoi.model.ProductVersion;
import vn.hieu4tuoi.model.Promotion;
import vn.hieu4tuoi.model.RequestForExchange;
import vn.hieu4tuoi.model.ReturnRequest;
import vn.hieu4tuoi.model.User;
import vn.hieu4tuoi.model.WarrantyClaim;
import vn.hieu4tuoi.repository.CartItemRepository;
import vn.hieu4tuoi.repository.OrderItemRepository;
import vn.hieu4tuoi.repository.ProductColorVersionRepository;
import vn.hieu4tuoi.repository.ProductItemRepository;
import vn.hieu4tuoi.repository.ProductVersionRepository;
import vn.hieu4tuoi.repository.PromotionRepository;
import vn.hieu4tuoi.repository.UserRepository;
import vn.hieu4tuoi.repository.ProductRepository;
import vn.hieu4tuoi.repository.ImageRepository;
import vn.hieu4tuoi.repository.ReturnRequestRepository;
import vn.hieu4tuoi.repository.RequestForExchangeRepository;
import vn.hieu4tuoi.repository.WarrantyClaimRepository;

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
        private final ProductItemRepository productItemRepository;
        private final UserRepository userRepository;
        private final ImageRepository imageRepository;
        private final ObjectMapper objectMapper;        
        private final ReturnRequestRepository returnRequestRepository;
        private final RequestForExchangeRepository requestForExchangeRepository;
        private final WarrantyClaimRepository warrantyClaimRepository;

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

        /**
         * Tạo đơn hàng thông qua AI Chatbot
         * Nhận thông tin sản phẩm (productColorId, quantity) và thông tin giao hàng từ
         * AI Tool
         * 
         * @param request thông tin đặt hàng từ AI Tool
         * @return thông báo kết quả đặt hàng
         */
        @Override
        @Transactional
        public String createOrderByAI(OrderByAIRequest request) {
                // Lấy thông tin user từ security context
                String userId = SecurityUtils.getCurrentUserId();
                String userFullName = SecurityUtils.getCurrentUserFullName();
                if (userId == null) {
                        return "Vui lòng đăng nhập để đặt hàng";
                }

                // Parse số lượng, mặc định là 1
                int quantity = 1;
                try {
                        if (request.getQuantity() != null && !request.getQuantity().isEmpty()) {
                                quantity = Integer.parseInt(request.getQuantity());
                        }
                } catch (NumberFormatException e) {
                        return "Số lượng sản phẩm không hợp lệ";
                }
                if (quantity <= 0) {
                        return "Số lượng sản phẩm phải lớn hơn 0";
                }

                // Lấy ProductColorVersion từ productColorId
                ProductColorVersion productColorVersion = productColorVersionRepository
                                .findByIdAndIsDeleted(request.getProductColorId(), false);
                if (productColorVersion == null) {
                        return "Sản phẩm không tồn tại hoặc đã ngừng kinh doanh";
                }

                // Kiểm tra số lượng tồn kho
                if (quantity > productColorVersion.getTotalStock().intValue()) {
                        return "Số lượng sản phẩm trong kho không đủ. Hiện tại chỉ còn "
                                        + productColorVersion.getTotalStock() + " sản phẩm";
                }

                // Lấy ProductVersion để tính giá
                ProductVersion productVersion = productVersionRepository
                                .findByIdAndIsDeleted(productColorVersion.getProductVersionId(), false);
                if (productVersion == null) {
                        return "Phiên bản sản phẩm không tồn tại";
                }

                // Lấy Product để lấy tên sản phẩm
                Product product = productRepository.findByIdAndIsDeleted(productVersion.getProductId(), false);
                if (product == null) {
                        return "Sản phẩm không tồn tại";
                }

                // Tính giá sau khuyến mãi
                long discountedPrice = productVersion.getPrice();
                Promotion promotion = null;
                if (productVersion.getPromotionId() != null) {
                        promotion = promotionRepository.findByIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
                                        productVersion.getPromotionId(), LocalDateTime.now(), false);
                }
                if (promotion != null) {
                        double discountPercent = promotion.getDiscount();
                        double discountAmount = productVersion.getPrice() * discountPercent / 100;
                        if (discountAmount > promotion.getMaximumDiscountAmount()) {
                                discountAmount = promotion.getMaximumDiscountAmount();
                        }
                        discountedPrice = (long) Math.round((productVersion.getPrice() - discountAmount) / 1000.0)
                                        * 1000;
                }

                // Parse payment method
                PaymentMethod paymentMethod;
                String paymentMethodStr = request.getPaymentMethod();
                if (paymentMethodStr == null || paymentMethodStr.isEmpty()) {
                        return "Vui lòng chọn phương thức thanh toán";
                }
                if (paymentMethodStr.equalsIgnoreCase("cash") || paymentMethodStr.equalsIgnoreCase("tiền mặt")) {
                        paymentMethod = PaymentMethod.CASH;
                } else if (paymentMethodStr.equalsIgnoreCase("bank")
                                || paymentMethodStr.equalsIgnoreCase("chuyển khoản")) {
                        paymentMethod = PaymentMethod.BANK_TRANSFER;
                } else {
                        return "Phương thức thanh toán không hợp lệ. Vui lòng chọn 'cash' (tiền mặt) hoặc 'bank' (chuyển khoản)";
                }

                // Tạo Order
                Order order = new Order();
                order.setUserId(userId);
                order.setPhoneNumber(request.getPhoneNumber());
                order.setAddress(request.getAddress());
                order.setNote(request.getNote() != null ? request.getNote() : "");
                order.setPaymentMethod(paymentMethod);
                order.setStatus(OrderStatus.PENDING);
                order.setPaymentStatus(PaymentStatus.UNPAID);
                order.setFullTextSearch(StringUtils.toFullTextSearch(userFullName + " " + order.getPhoneNumber()));
                orderRepository.save(order);

                // Tạo OrderItems
                List<OrderItem> orderItems = new ArrayList<>();
                for (int i = 0; i < quantity; i++) {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrderId(order.getId());
                        orderItem.setPrice(productVersion.getPrice());
                        orderItem.setDiscountedPrice(discountedPrice);
                        orderItem.setProductColorVersionId(productColorVersion.getId());
                        orderItems.add(orderItem);
                }
                orderItemRepository.saveAll(orderItems);

                // Cập nhật số lượng tồn kho và đã bán
                productColorVersion.setTotalStock(productColorVersion.getTotalStock() - quantity);
                productColorVersion.setTotalSold(productColorVersion.getTotalSold() + quantity);
                productVersion.setTotalSold(productVersion.getTotalSold() + quantity);
                productVersionRepository.save(productVersion);
                productColorVersionRepository.save(productColorVersion);

                // Tính tổng tiền
                long totalPrice = discountedPrice * quantity;

                // Tạo response object và convert sang JSON
                try {
                        OrderByAIResponse response = new OrderByAIResponse();
                        response.setMessage("order success!");
                        response.setOrderId(order.getId());
                        response.setTotalPrice(totalPrice);
                        response.setPaymentMethod(paymentMethod);
                        return objectMapper.writeValueAsString(response);
                } catch (Exception e) {
                        // Nếu có lỗi khi convert JSON, trả về string như cũ
                        return "Có lỗi xảy ra khi đặt hàng.";
                }
        }

        @Override
        public PageResponse<List<OrderAdminResponse>> getOrderList(int page, int size, String sort, String keyword,
                        OrderStatus status) {
                // Build pageable với sắp xếp
                Pageable pageable = CommonUtils.createPageable(page, size, sort);
                String keywordSearch = CommonUtils.createKeywordSearch(keyword);
                Page<Order> orderPage = orderRepository.findAllByFullTextSearchOrIdAndStatus(keywordSearch, null,
                                status, pageable);

                // order item
                List<String> orderIds = orderPage.getContent().stream().map(Order::getId).toList();
                List<OrderItem> orderItems = orderItemRepository.findByOrderIdInAndIsDeleted(orderIds, false);
                Map<String, List<OrderItem>> orderItemMap = orderItems.stream()
                                .collect(Collectors.groupingBy(OrderItem::getOrderId));

                // ds product color version
                List<String> productColorVersionIds = orderItems.stream()
                                .map(OrderItem::getProductColorVersionId)
                                .toList();
                List<ProductColorVersion> productColorVersions = productColorVersionRepository
                                .findAllByIdInAndIsDeleted(productColorVersionIds, false);
                Map<String, ProductColorVersion> productColorVersionMap = productColorVersions.stream()
                                .collect(Collectors.toMap(ProductColorVersion::getId, Function.identity()));

                // ds product version
                List<String> productVersionIds = productColorVersions.stream()
                                .map(ProductColorVersion::getProductVersionId)
                                .toList();
                List<ProductVersion> productVersions = productVersionRepository
                                .findAllByIdInAndIsDeleted(productVersionIds, false);
                Map<String, ProductVersion> productVersionMap = productVersions.stream()
                                .collect(Collectors.toMap(ProductVersion::getId, Function.identity()));

                // ds product
                List<String> productIds = productVersions.stream()
                                .map(ProductVersion::getProductId)
                                .toList();
                List<Product> products = productRepository.findAllByIdInAndIsDeleted(productIds, false);
                Map<String, Product> productMap = products.stream()
                                .collect(Collectors.toMap(Product::getId, Function.identity()));

                // ds customer
                List<String> customerIds = orderPage.getContent().stream().map(Order::getUserId).toList();
                List<User> customers = userRepository.findAllByIdInAndIsDeleted(customerIds, false);
                Map<String, User> customerMap = customers.stream()
                                .collect(Collectors.toMap(User::getId, Function.identity()));

                // set thông tin order và sản phẩm, customer
                List<OrderAdminResponse> orderResponseList = new ArrayList<>();
                for (Order order : orderPage.getContent()) {
                        OrderAdminResponse orderResponse = orderMapper.entityToResponse(order);
                        // get list order item của order
                        List<OrderItem> orderItemsOrder = orderItemMap.get(order.getId());
                        // set thông tin sản phẩm và tính tổng tiền kèm số lượng từng sản phẩm
                        // List<ProductOrderResponse> productOrderResponseList = new ArrayList<>();
                        // Map<String, Integer> productQuantityMap = new HashMap<>();
                        Map<String, ProductOrderAdminResponse> productOrderResponseMap = new HashMap<>();
                        long totalPrice = 0;
                        for (OrderItem orderItem : orderItemsOrder) {
                                ProductColorVersion productColorVersion = productColorVersionMap
                                                .get(orderItem.getProductColorVersionId());
                                ProductVersion productVersion = productVersionMap
                                                .get(productColorVersion.getProductVersionId());
                                Product product = productMap.get(productVersion.getProductId());
                                ProductOrderAdminResponse productOrderResponse = new ProductOrderAdminResponse();
                                productOrderResponse.setOrderItemId(orderItem.getId());
                                productOrderResponse.setProductName(product.getName());
                                productOrderResponse.setProductVersionName(productVersion.getName());
                                productOrderResponse.setColorName(productColorVersion.getColor());
                                // nếu chưa có số lượng của sản phẩm trong productQuantityMap thì set là 1, nếu
                                // có thì tăng lên 1
                                int quantity = 1;
                                if (productOrderResponseMap.containsKey(orderItem.getProductColorVersionId())) {
                                        quantity = productOrderResponseMap.get(orderItem.getProductColorVersionId())
                                                        .getQuantity() + 1;
                                }
                                productOrderResponse.setQuantity(quantity);
                                productOrderResponseMap.put(orderItem.getProductColorVersionId(), productOrderResponse);
                                // tính tổng tiền của sản phẩm ( giá sau khuyến mãi * số lượng)
                                // productOrderResponse.setTotalPrice(orderItem.getDiscountedPrice() *
                                // quantity);
                                totalPrice += orderItem.getDiscountedPrice();
                                // productOrderResponseList.add(productOrderResponse);
                        }
                        // set thông tin customer
                        User customer = customerMap.get(order.getUserId());
                        CustomerOrderAdminResponse customerOrderResponse = new CustomerOrderAdminResponse();
                        customerOrderResponse.setId(customer.getId());
                        customerOrderResponse.setName(customer.getFullName());
                        customerOrderResponse.setPhoneNumber(customer.getPhoneNumber());
                        orderResponse.setCustomer(customerOrderResponse);
                        orderResponse.setProducts(productOrderResponseMap.values().stream().toList());
                        orderResponse.setTotalPrice(totalPrice);
                        orderResponseList.add(orderResponse);
                }
                return PageResponse.<List<OrderAdminResponse>>builder()
                                .pageNo(orderPage.getNumber())
                                .pageSize(orderPage.getSize())
                                .totalPage(orderPage.getTotalPages())
                                .items(orderResponseList)
                                .build();
        }

        @Override
        public List<OrderResponse> getOrderListByCurrentUser() {
                String userId = SecurityUtils.getCurrentUserId();
                if (userId == null) {
                        throw new UnauthorizedException("Vui lòng đăng nhập để xem danh sách đơn hàng");
                }
                List<Order> orderList = orderRepository.findAllByUserIdAndIsDeletedOrderByCreatedAtDesc(userId, false);

                // order item
                List<String> orderIds = orderList.stream().map(Order::getId).toList();
                List<OrderItem> orderItems = orderItemRepository.findByOrderIdInAndIsDeleted(orderIds, false);
                Map<String, List<OrderItem>> orderItemMap = orderItems.stream()
                                .collect(Collectors.groupingBy(OrderItem::getOrderId));

                // ds product color version
                List<String> productColorVersionIds = orderItems.stream()
                                .map(OrderItem::getProductColorVersionId)
                                .toList();
                List<ProductColorVersion> productColorVersions = productColorVersionRepository
                                .findAllByIdInAndIsDeleted(productColorVersionIds, false);
                Map<String, ProductColorVersion> productColorVersionMap = productColorVersions.stream()
                                .collect(Collectors.toMap(ProductColorVersion::getId, Function.identity()));

                // ds product version
                List<String> productVersionIds = productColorVersions.stream()
                                .map(ProductColorVersion::getProductVersionId)
                                .toList();
                List<ProductVersion> productVersions = productVersionRepository
                                .findAllByIdInAndIsDeleted(productVersionIds, false);
                Map<String, ProductVersion> productVersionMap = productVersions.stream()
                                .collect(Collectors.toMap(ProductVersion::getId, Function.identity()));

                // ds product
                List<String> productIds = productVersions.stream()
                                .map(ProductVersion::getProductId)
                                .toList();
                List<Product> products = productRepository.findAllByIdInAndIsDeleted(productIds, false);
                Map<String, Product> productMap = products.stream()
                                .collect(Collectors.toMap(Product::getId, Function.identity()));

                // ds ảnh mặc định
                List<Image> imageList = imageRepository.findAllByIsDefaultAndProductIdInAndIsDeleted(true, productIds,
                                false);
                Map<String, String> imageMap = imageList.stream()
                                .collect(Collectors.toMap(Image::getProductId, Image::getUrl));

                // customer
                User customer = userRepository.findByIdAndIsDeleted(userId, false);

                // set thông tin order và sản phẩm, customer
                List<OrderResponse> orderResponseList = new ArrayList<>();
                for (Order order : orderList) {
                        OrderResponse orderResponse = new OrderResponse();
                        orderResponse.setId(order.getId());
                        orderResponse.setStatus(order.getStatus());
                        orderResponse.setPaymentMethod(order.getPaymentMethod());
                        orderResponse.setPaymentStatus(order.getPaymentStatus());
                        orderResponse.setNote(order.getNote());
                        orderResponse.setAddress(order.getAddress());
                        orderResponse.setPhoneNumber(customer.getPhoneNumber());
                        orderResponse.setCreatedAt(order.getCreatedAt());
                        orderResponse.setModifiedAt(order.getModifiedAt());
                        // get list order item của order
                        List<OrderItem> orderItemsOrder = orderItemMap.get(order.getId());
                        List<ProductOrderResponse> productOrderResponseList = new ArrayList<>();
                        // Map<String, Integer> productQuantityMap = new HashMap<>();
                        long totalPrice = 0;
                        for (OrderItem orderItem : orderItemsOrder) {
                                ProductColorVersion productColorVersion = productColorVersionMap
                                                .get(orderItem.getProductColorVersionId());
                                ProductVersion productVersion = productVersionMap
                                                .get(productColorVersion.getProductVersionId());
                                Product product = productMap.get(productVersion.getProductId());
                                ProductOrderResponse productOrderResponse = new ProductOrderResponse();
                                productOrderResponse.setOrderItemId(orderItem.getId());
                                productOrderResponse.setProductName(product.getName());
                                productOrderResponse.setProductVersionName(productVersion.getName());
                                productOrderResponse.setColorName(productColorVersion.getColor());
                                productOrderResponse.setPrice(orderItem.getDiscountedPrice());
                                productOrderResponse.setImeiOrSerial(orderItem.getProductItemId());
                                productOrderResponse.setImageUrl(imageMap.get(productVersion.getProductId()));
                                productOrderResponse.setSlug(productVersion.getSlug());
                                totalPrice += orderItem.getDiscountedPrice();
                                //nếu return id != null thì set return request type
                                if (orderItem.getReturnRequestId() != null) {
                                        productOrderResponse.setReturnRequestType(ReturnRequestType.RETURN);
                                }
                                if (orderItem.getRequestForExchangeId() != null) {
                                        productOrderResponse.setReturnRequestType(ReturnRequestType.EXCHANGE);
                                }
                                if (orderItem.getWarrantyClaimId() != null) {
                                        productOrderResponse.setReturnRequestType(ReturnRequestType.WARRANTY);  
                                }
                                productOrderResponseList.add(productOrderResponse);
                        }
                        // set thông tin customer
                        CustomerOrderAdminResponse customerOrderResponse = new CustomerOrderAdminResponse();
                        customerOrderResponse.setId(customer.getId());
                        customerOrderResponse.setName(customer.getFullName());
                        customerOrderResponse.setPhoneNumber(customer.getPhoneNumber());
                        // orderResponse.setCustomer(customerOrderResponse);
                        orderResponse.setProducts(productOrderResponseList);
                        ;
                        orderResponse.setTotalPrice(totalPrice);
                        orderResponseList.add(orderResponse);
                }
                return orderResponseList;
        }

        @Override
        public OrderDetailResponse getOrderDetail(String id) {
                Order order = orderRepository.findByIdAndIsDeleted(id, false);
                if (order == null) {
                        throw new ResourceNotFoundException("Đơn hàng không tồn tại");
                }

                // order item
                List<OrderItem> orderItems = orderItemRepository.findByOrderIdAndIsDeleted(order.getId(), false);

                // ds product item
                List<String> productItemIds = orderItems.stream().map(OrderItem::getProductItemId).toList();
                List<ProductItem> productItems = productItemRepository.findAllByIdInAndIsDeleted(productItemIds, false);
                Map<String, ProductItem> productItemMap = productItems.stream()
                                .collect(Collectors.toMap(ProductItem::getId, Function.identity()));

                // ds product color version
                List<String> productColorVersionIds = orderItems.stream().map(OrderItem::getProductColorVersionId)
                                .toList();
                List<ProductColorVersion> productColorVersions = productColorVersionRepository
                                .findAllByIdInAndIsDeleted(productColorVersionIds, false);
                Map<String, ProductColorVersion> productColorVersionMap = productColorVersions.stream()
                                .collect(Collectors.toMap(ProductColorVersion::getId, Function.identity()));

                // ds product version
                List<String> productVersionIds = productColorVersions.stream()
                                .map(ProductColorVersion::getProductVersionId)
                                .toList();
                List<ProductVersion> productVersions = productVersionRepository
                                .findAllByIdInAndIsDeleted(productVersionIds, false);
                Map<String, ProductVersion> productVersionMap = productVersions.stream()
                                .collect(Collectors.toMap(ProductVersion::getId, Function.identity()));

                // ds product
                List<String> productIds = productVersions.stream()
                                .map(ProductVersion::getProductId)
                                .toList();
                List<Product> products = productRepository.findAllByIdInAndIsDeleted(productIds, false);
                Map<String, Product> productMap = products.stream()
                                .collect(Collectors.toMap(Product::getId, Function.identity()));

                // customer
                User customer = userRepository.findByIdAndIsDeleted(order.getUserId(), false);
                CustomerOrderAdminResponse customerOrderResponse = new CustomerOrderAdminResponse();
                customerOrderResponse.setId(customer.getId());
                customerOrderResponse.setName(customer.getFullName());
                customerOrderResponse.setPhoneNumber(customer.getPhoneNumber());

                // set thông tin order
                OrderDetailResponse orderDetailResponse = orderMapper.entityToDetailResponse(order);

                // set thông tin customer
                orderDetailResponse.setCustomer(customerOrderResponse);

                // set thông tin sản phẩm
                List<ProductOrderDetailResponse> productOrderDetailResponseList = new ArrayList<>();
                long totalPrice = 0;
                for (OrderItem orderItem : orderItems) {
                        ProductColorVersion productColorVersion = productColorVersionMap
                                        .get(orderItem.getProductColorVersionId());
                        ProductVersion productVersion = productVersionMap
                                        .get(productColorVersion.getProductVersionId());
                        Product product = productMap.get(productVersion.getProductId());
                        ProductOrderDetailResponse productOrderDetailResponse = new ProductOrderDetailResponse();
                        productOrderDetailResponse.setOrderItemId(orderItem.getId());
                        productOrderDetailResponse.setProductName(product.getName());
                        productOrderDetailResponse.setProductVersionName(productVersion.getName());
                        productOrderDetailResponse.setColorName(productColorVersion.getColor());
                        productOrderDetailResponse.setPrice(orderItem.getDiscountedPrice());
                        if (productItemMap.containsKey(orderItem.getProductItemId())) {
                                productOrderDetailResponse.setImeiOrSerial(
                                                productItemMap.get(orderItem.getProductItemId()).getImeiOrSerial());
                        }
                        productOrderDetailResponseList.add(productOrderDetailResponse);
                        totalPrice += orderItem.getDiscountedPrice();
                }
                orderDetailResponse.setProducts(productOrderDetailResponseList);
                orderDetailResponse.setTotalPrice(totalPrice);
                return orderDetailResponse;
        }

        @Override
        @Transactional
        public void updateOrderStatus(String id, UpdateOrderStatusRequest request) {
                Order order = orderRepository.findByIdAndIsDeleted(id, false);
                if (order == null) {
                        throw new ResourceNotFoundException("Đơn hàng không tồn tại");
                }

                // xác nhận đơn hàng
                if (request.getStatus() == OrderStatus.CONFIRMED) {
                        if (order.getStatus() == OrderStatus.PENDING) {
                                order.setStatus(OrderStatus.CONFIRMED);
                                orderRepository.save(order);
                        } else {
                                throw new BadRequestException("Trạng thái cập nhật không phù hợp");
                        }
                }

                // gửi đơn hàng, set imeiorserial cho từng sản phẩm
                if (request.getStatus() == OrderStatus.SHIPPING) {
                        if (order.getStatus() == OrderStatus.CONFIRMED
                                        && request.getProductItemImeiList() != null
                                        && !request.getProductItemImeiList().isEmpty()) {
                                List<String> orderItemIds = request.getProductItemImeiList().stream()
                                                .map(ProductItemImeiRequest::getOrderItemId).toList();
                                List<OrderItem> orderItems = orderItemRepository.findAllByIdInAndIsDeleted(orderItemIds,
                                                false);
                                if (orderItems.size() != request.getProductItemImeiList().size()) {
                                        throw new ResourceNotFoundException("Sản phẩm không tồn tại");
                                }
                                Map<String, OrderItem> orderItemMap = orderItems.stream()
                                                .collect(Collectors.toMap(OrderItem::getId, Function.identity()));

                                List<String> productImeis = request.getProductItemImeiList().stream()
                                                .map(ProductItemImeiRequest::getImeiOrSerial).toList();
                                // chỉ lấy product item có status là IN_STOCK
                                List<ProductItem> productItems = productItemRepository
                                                .findAllByImeiOrSerialInAndStatusAndIsDeleted(productImeis,
                                                                ProductItemStatus.IN_STOCK, false);
                                if (productItems.size() != request.getProductItemImeiList().size()) {
                                        throw new ResourceNotFoundException("Sản phẩm không tồn tại");
                                }
                                Map<String, ProductItem> productItemMap = productItems.stream()
                                                .collect(Collectors.toMap(ProductItem::getImeiOrSerial,
                                                                Function.identity()));

                                // số lượng productItem phải = số lượng orderItem
                                if (productItems.size() != orderItems.size()) {
                                        throw new BadRequestException("Imei không hợp lệ");
                                }

                                // kiểm tra và lần lượt set imei or serial
                                for (ProductItemImeiRequest productItemImeiRequest : request.getProductItemImeiList()) {
                                        OrderItem orderItem = orderItemMap.get(productItemImeiRequest.getOrderItemId());
                                        if (orderItem == null) {
                                                throw new ResourceNotFoundException("Đơn hàng không tồn tại");
                                        }
                                        ProductItem productItem = productItemMap
                                                        .get(productItemImeiRequest.getImeiOrSerial());
                                        if (productItem == null) {
                                                throw new ResourceNotFoundException("Sản phẩm không tồn tại");
                                        }
                                        // nếu imei đúng với color version thì set imei or serial cho order item( tránh
                                        // tình trạng set imei của sản phẩm khác vào order item)
                                        if (productItem.getProductColorVersionId()
                                                        .equals(orderItem.getProductColorVersionId())) {
                                                orderItem.setProductItemId(productItem.getId());
                                                productItem.setStatus(ProductItemStatus.SOLD);

                                        } else {
                                                throw new BadRequestException(
                                                                "IMEI hoặc serial không đúng với sản phẩm");
                                        }
                                }

                                // lưu product item và order item
                                productItemRepository.saveAll(productItems);
                                orderItemRepository.saveAll(orderItems);
                                order.setStatus(OrderStatus.SHIPPING);
                                orderRepository.save(order);
                        } else {
                                throw new BadRequestException("Trạng thái cập nhật không phù hợp");
                        }
                }

                // xác nhận đã giao
                if (request.getStatus() == OrderStatus.DELIVERED) {
                        if (order.getStatus() == OrderStatus.SHIPPING) {
                                order.setStatus(OrderStatus.DELIVERED);
                                order.setPaymentStatus(PaymentStatus.PAID);
                                // get ds product item của order
                                List<OrderItem> orderItems = orderItemRepository
                                                .findByOrderIdAndIsDeleted(order.getId(), false);
                                List<String> productItemIds = orderItems.stream().map(OrderItem::getProductItemId)
                                                .toList();
                                List<ProductItem> productItems = productItemRepository
                                                .findAllByIdInAndIsDeleted(productItemIds, false);
                                // cập nhật thời gian hết bảo hành cho từng product item
                                for (ProductItem productItem : productItems) {
                                        productItem.setWarrantyExpirationDate(LocalDate.now().plusMonths(12));
                                        productItem.setWarrantyActivationDate(LocalDate.now());
                                }
                                productItemRepository.saveAll(productItems);
                                orderRepository.save(order);
                        } else {
                                throw new BadRequestException("Trạng thái cập nhật không phù hợp");
                        }
                }

                // giao thất bại:
                if (request.getStatus() == OrderStatus.DELIVERED_FAILED) {
                        if (order.getStatus() == OrderStatus.SHIPPING) {
                                order.setStatus(OrderStatus.DELIVERED_FAILED);
                                orderRepository.save(order);

                                List<OrderItem> orderItems = orderItemRepository
                                                .findByOrderIdAndIsDeleted(order.getId(), false);
                                List<String> imeiOrSerials = orderItems.stream().map(OrderItem::getProductItemId)
                                                .toList();
                                List<ProductItem> productItems = productItemRepository
                                                .findAllByImeiOrSerialInAndStatusAndIsDeleted(imeiOrSerials, null,
                                                                false);
                                // cập nhật ds product item về IN_STOCK
                                for (ProductItem productItem : productItems) {
                                        productItem.setStatus(ProductItemStatus.IN_STOCK);
                                }
                                productItemRepository.saveAll(productItems);
                                // cập nhật số lượng tồn kho và đã bán của product color version và product
                                // Map<String, Integer> productColorVersionQuantityMap = new HashMap<>();
                                // // Map<String, Integer> productVersionQuantityMap = new HashMap<>();
                                // for (OrderItem orderItem : orderItems) {
                                // ProductColorVersion productColorVersion = productColorVersionMap
                                // .get(orderItem.getProductColorVersionId());
                                // ProductVersion productVersion = productVersionMap
                                // .get(productColorVersion.getProductVersionId());
                                // }

                        } else {
                                throw new BadRequestException("Trạng thái cập nhật không phù hợp");
                        }
                }

                // hủy đơn hàng
                if (request.getStatus() == OrderStatus.CANCELLED) {
                        if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED) {
                                order.setStatus(OrderStatus.CANCELLED);
                                orderRepository.save(order);
                                List<OrderItem> orderItems = orderItemRepository
                                                .findByOrderIdAndIsDeleted(order.getId(), false);
                                List<String> imeiOrSerials = orderItems.stream().map(OrderItem::getProductItemId)
                                                .toList();
                                List<ProductItem> productItems = productItemRepository
                                                .findAllByImeiOrSerialInAndStatusAndIsDeleted(imeiOrSerials, null,
                                                                false);
                                // cập nhật ds product item về IN_STOCK
                                for (ProductItem productItem : productItems) {
                                        productItem.setStatus(ProductItemStatus.IN_STOCK);
                                }
                                productItemRepository.saveAll(productItems);
                                // cập nhật số lượng tồn kho và đã bán của product color version và product
                                // version

                        } else {
                                throw new BadRequestException("Trạng thái cập nhật không phù hợp");
                        }
                }
        }

        @Override
        @Transactional
        public void createReturnRequest(ReturnRequestRequest request) {

                String userId = SecurityUtils.getCurrentUserId();
                if (userId == null) {
                        throw new UnauthorizedException("Vui lòng đăng nhập để tạo yêu cầu");
                }
                // tạo đơn đổi trả bảo hành, cần check cả 3 id null thì mới được gửi yêu cầu
                OrderItem orderItem = orderItemRepository.findByIdAndIsDeleted(request.getOrderItemId(), false);
                if (orderItem == null) {
                        throw new ResourceNotFoundException("Đơn hàng không tồn tại");
                }
                
                // Kiểm tra orderItem có thuộc về user hiện tại hay không
                Order order = orderRepository.findByIdAndIsDeleted(orderItem.getOrderId(), false);
                if (order == null) {
                        throw new ResourceNotFoundException("Đơn hàng không tồn tại");
                }
                if (!order.getUserId().equals(userId)) {
                        throw new BadRequestException("Bạn không có quyền tạo yêu cầu");
                }
                
                if (orderItem.getReturnRequestId() != null || orderItem.getRequestForExchangeId() != null || orderItem.getWarrantyClaimId() != null) {
                        throw new BadRequestException("Đơn hàng đã có yêu cầu đổi trả bảo hành");
                }
                //nếu type là trả hàng
                if (request.getReturnRequestType() == ReturnRequestType.RETURN) {
                        createReturnRequest(orderItem , userId, request.getReason());
                }
                //nếu type là đổi trả
                if (request.getReturnRequestType() == ReturnRequestType.EXCHANGE) {
                        createRequestForExchange(orderItem , userId, request.getReason(), request.getPhoneNumber(), request.getAddress());
                }
                //nếu type là bảo hành
                if (request.getReturnRequestType() == ReturnRequestType.WARRANTY) {
                        createWarrantyClaim(orderItem , userId, request.getReason(), request.getPhoneNumber(), request.getAddress());
                }
        }

        /**
         * Tạo yêu cầu trả hàng
         */
        private void createReturnRequest(OrderItem orderItem, String userId, String reason) {
                ReturnRequest returnRequest = new ReturnRequest();
                returnRequest.setOrderItemId(orderItem.getId());
                returnRequest.setReason(reason);
                returnRequest.setUserId(userId);
                returnRequestRepository.save(returnRequest);

                //lưu order item
                orderItem.setReturnRequestId(returnRequest.getId());
                orderItemRepository.save(orderItem);
        }

        /**
         * Tạo yêu cầu đổi hàng
         */
        private void createRequestForExchange(OrderItem orderItem, String userId, String reason, String phoneNumber, String address) {
                RequestForExchange requestForExchange = new RequestForExchange();
                requestForExchange.setOrderItemId(orderItem.getId());
                requestForExchange.setReason(reason);
                requestForExchange.setUserId(userId);
                requestForExchange.setPhoneNumber(phoneNumber);
                requestForExchange.setAddress(address);
                requestForExchangeRepository.save(requestForExchange);

                //lưu order item
                orderItem.setRequestForExchangeId(requestForExchange.getId());
                orderItemRepository.save(orderItem);
        }

        /**
         * Tạo yêu cầu bảo hành
         */
        private void createWarrantyClaim(OrderItem orderItem, String userId, String reason, String phoneNumber, String address) {
                WarrantyClaim warrantyClaim = new WarrantyClaim();
                warrantyClaim.setOrderItemId(orderItem.getId());
                warrantyClaim.setReason(reason);
                warrantyClaim.setUserId(userId);
                warrantyClaim.setPhoneNumber(phoneNumber);
                warrantyClaim.setAddress(address);
                warrantyClaimRepository.save(warrantyClaim);
                
                //lưu order item
                orderItem.setWarrantyClaimId(warrantyClaim.getId());
                orderItemRepository.save(orderItem);
        }

        @Override
        public List<WarrantyClaimResponse> getWarrantyClaimListByCurrentUser() {
                String userId = SecurityUtils.getCurrentUserId();
                if (userId == null) {
                        throw new UnauthorizedException("Vui lòng đăng nhập để xem danh sách bảo hành");
                }
                
                // Lấy danh sách yêu cầu bảo hành của user
                List<WarrantyClaim> warrantyClaims = warrantyClaimRepository.findByUserIdAndIsDeletedOrderByCreatedAtDesc(userId, false);
                
                // Lấy danh sách orderItemId
                List<String> orderItemIds = warrantyClaims.stream()
                                .map(WarrantyClaim::getOrderItemId)
                                .toList();
                
                // Lấy danh sách orderItem
                List<OrderItem> orderItems = orderItemRepository.findAllByIdInAndIsDeleted(orderItemIds, false);
                Map<String, OrderItem> orderItemMap = orderItems.stream()
                                .collect(Collectors.toMap(OrderItem::getId, Function.identity()));
                
                // Lấy danh sách orderId để lấy thông tin order
                List<String> orderIds = orderItems.stream()
                                .map(OrderItem::getOrderId)
                                .distinct()
                                .toList();
                List<Order> orders = orderRepository.findAllByIdInAndIsDeleted(orderIds, false);
                Map<String, Order> orderMap = orders.stream()
                                .collect(Collectors.toMap(Order::getId, Function.identity()));
                
                // Lấy danh sách productColorVersionId
                List<String> productColorVersionIds = orderItems.stream()
                                .map(OrderItem::getProductColorVersionId)
                                .distinct()
                                .toList();
                List<ProductColorVersion> productColorVersions = productColorVersionRepository
                                .findAllByIdInAndIsDeleted(productColorVersionIds, false);
                Map<String, ProductColorVersion> productColorVersionMap = productColorVersions.stream()
                                .collect(Collectors.toMap(ProductColorVersion::getId, Function.identity()));
                
                // Lấy danh sách productVersionId
                List<String> productVersionIds = productColorVersions.stream()
                                .map(ProductColorVersion::getProductVersionId)
                                .distinct()
                                .toList();
                List<ProductVersion> productVersions = productVersionRepository
                                .findAllByIdInAndIsDeleted(productVersionIds, false);
                Map<String, ProductVersion> productVersionMap = productVersions.stream()
                                .collect(Collectors.toMap(ProductVersion::getId, Function.identity()));
                
                // Lấy danh sách productId
                List<String> productIds = productVersions.stream()
                                .map(ProductVersion::getProductId)
                                .distinct()
                                .toList();
                List<Product> products = productRepository.findAllByIdInAndIsDeleted(productIds, false);
                Map<String, Product> productMap = products.stream()
                                .collect(Collectors.toMap(Product::getId, Function.identity()));
                
                // Lấy danh sách ảnh mặc định
                List<Image> imageList = imageRepository.findAllByIsDefaultAndProductIdInAndIsDeleted(true, productIds, false);
                Map<String, String> imageMap = imageList.stream()
                                .collect(Collectors.toMap(Image::getProductId, Image::getUrl));
                
                // Lấy danh sách productItemId để lấy imeiOrSerial
                List<String> productItemIds = orderItems.stream()
                                .map(OrderItem::getProductItemId)
                                .filter(id -> id != null)
                                .distinct()
                                .toList();
                List<ProductItem> productItems = productItemRepository.findAllByIdInAndIsDeleted(productItemIds, false);
                Map<String, ProductItem> productItemMap = productItems.stream()
                                .collect(Collectors.toMap(ProductItem::getId, Function.identity()));
                
                // Tạo danh sách response
                List<WarrantyClaimResponse> warrantyClaimResponseList = new ArrayList<>();
                for (WarrantyClaim warrantyClaim : warrantyClaims) {
                        WarrantyClaimResponse response = new WarrantyClaimResponse();
                        response.setId(warrantyClaim.getId());
                        response.setStatus(warrantyClaim.getStatus());
                        response.setReason(warrantyClaim.getReason());
                        response.setPhoneNumber(warrantyClaim.getPhoneNumber());
                        response.setAddress(warrantyClaim.getAddress());
                        response.setCreatedAt(warrantyClaim.getCreatedAt());
                        
                        // Lấy orderItem và order
                        OrderItem orderItem = orderItemMap.get(warrantyClaim.getOrderItemId());
                        if (orderItem != null) {
                                Order order = orderMap.get(orderItem.getOrderId());
                                if (order != null) {
                                        response.setOrderId(order.getId());
                                }
                                
                                // Lấy thông tin sản phẩm
                                ProductColorVersion productColorVersion = productColorVersionMap.get(orderItem.getProductColorVersionId());
                                if (productColorVersion != null) {
                                        ProductVersion productVersion = productVersionMap.get(productColorVersion.getProductVersionId());
                                        if (productVersion != null) {
                                                Product product = productMap.get(productVersion.getProductId());
                                                if (product != null) {
                                                        ProductWarrantyResponse productResponse = new ProductWarrantyResponse();
                                                        productResponse.setOrderItemId(orderItem.getId());
                                                        productResponse.setProductName(product.getName());
                                                        productResponse.setProductVersionName(productVersion.getName());
                                                        productResponse.setColorName(productColorVersion.getColor());
                                                        productResponse.setImageUrl(imageMap.get(productVersion.getProductId()));
                                                        
                                                        // Lấy imeiOrSerial nếu có
                                                        if (orderItem.getProductItemId() != null) {
                                                                ProductItem productItem = productItemMap.get(orderItem.getProductItemId());
                                                                if (productItem != null) {
                                                                        productResponse.setImeiOrSerial(productItem.getImeiOrSerial());
                                                                }
                                                        }
                                                        
                                                        response.setProduct(productResponse);
                                                }
                                        }
                                }
                        }
                        
                        warrantyClaimResponseList.add(response);
                }
                
                return warrantyClaimResponseList;
        }

        @Override
        public List<ReturnRequestResponse> getReturnRequestListByCurrentUser() {
                String userId = SecurityUtils.getCurrentUserId();
                if (userId == null) {
                        throw new UnauthorizedException("Vui lòng đăng nhập để xem danh sách trả hàng");
                }
                
                // Lấy danh sách yêu cầu trả hàng của user
                List<ReturnRequest> returnRequests = returnRequestRepository.findByUserIdAndIsDeletedOrderByCreatedAtDesc(userId, false);
                
                // Lấy danh sách orderItemId
                List<String> orderItemIds = returnRequests.stream()
                                .map(ReturnRequest::getOrderItemId)
                                .toList();
                
                // Lấy danh sách orderItem
                List<OrderItem> orderItems = orderItemRepository.findAllByIdInAndIsDeleted(orderItemIds, false);
                Map<String, OrderItem> orderItemMap = orderItems.stream()
                                .collect(Collectors.toMap(OrderItem::getId, Function.identity()));
                
                // Lấy danh sách orderId để lấy thông tin order
                List<String> orderIds = orderItems.stream()
                                .map(OrderItem::getOrderId)
                                .distinct()
                                .toList();
                List<Order> orders = orderRepository.findAllByIdInAndIsDeleted(orderIds, false);
                Map<String, Order> orderMap = orders.stream()
                                .collect(Collectors.toMap(Order::getId, Function.identity()));
                
                // Lấy danh sách productColorVersionId
                List<String> productColorVersionIds = orderItems.stream()
                                .map(OrderItem::getProductColorVersionId)
                                .distinct()
                                .toList();
                List<ProductColorVersion> productColorVersions = productColorVersionRepository
                                .findAllByIdInAndIsDeleted(productColorVersionIds, false);
                Map<String, ProductColorVersion> productColorVersionMap = productColorVersions.stream()
                                .collect(Collectors.toMap(ProductColorVersion::getId, Function.identity()));
                
                // Lấy danh sách productVersionId
                List<String> productVersionIds = productColorVersions.stream()
                                .map(ProductColorVersion::getProductVersionId)
                                .distinct()
                                .toList();
                List<ProductVersion> productVersions = productVersionRepository
                                .findAllByIdInAndIsDeleted(productVersionIds, false);
                Map<String, ProductVersion> productVersionMap = productVersions.stream()
                                .collect(Collectors.toMap(ProductVersion::getId, Function.identity()));
                
                // Lấy danh sách productId
                List<String> productIds = productVersions.stream()
                                .map(ProductVersion::getProductId)
                                .distinct()
                                .toList();
                List<Product> products = productRepository.findAllByIdInAndIsDeleted(productIds, false);
                Map<String, Product> productMap = products.stream()
                                .collect(Collectors.toMap(Product::getId, Function.identity()));
                
                // Lấy danh sách ảnh mặc định
                List<Image> imageList = imageRepository.findAllByIsDefaultAndProductIdInAndIsDeleted(true, productIds, false);
                Map<String, String> imageMap = imageList.stream()
                                .collect(Collectors.toMap(Image::getProductId, Image::getUrl));
                
                // Lấy danh sách productItemId để lấy imeiOrSerial
                List<String> productItemIds = orderItems.stream()
                                .map(OrderItem::getProductItemId)
                                .filter(id -> id != null)
                                .distinct()
                                .toList();
                List<ProductItem> productItems = productItemRepository.findAllByIdInAndIsDeleted(productItemIds, false);
                Map<String, ProductItem> productItemMap = productItems.stream()
                                .collect(Collectors.toMap(ProductItem::getId, Function.identity()));
                
                // Tạo danh sách response
                List<ReturnRequestResponse> returnRequestResponseList = new ArrayList<>();
                for (ReturnRequest returnRequest : returnRequests) {
                        ReturnRequestResponse response = new ReturnRequestResponse();
                        response.setId(returnRequest.getId());
                        response.setStatus(returnRequest.getStatus());
                        response.setReason(returnRequest.getReason());
                        response.setPhoneNumber(returnRequest.getPhoneNumber());
                        response.setAddress(returnRequest.getAddress());
                        response.setCreatedAt(returnRequest.getCreatedAt());
                        
                        // Lấy orderItem và order
                        OrderItem orderItem = orderItemMap.get(returnRequest.getOrderItemId());
                        if (orderItem != null) {
                                Order order = orderMap.get(orderItem.getOrderId());
                                if (order != null) {
                                        response.setOrderId(order.getId());
                                }
                                
                                // Lấy thông tin sản phẩm
                                ProductColorVersion productColorVersion = productColorVersionMap.get(orderItem.getProductColorVersionId());
                                if (productColorVersion != null) {
                                        ProductVersion productVersion = productVersionMap.get(productColorVersion.getProductVersionId());
                                        if (productVersion != null) {
                                                Product product = productMap.get(productVersion.getProductId());
                                                if (product != null) {
                                                        ProductReturnResponse productResponse = new ProductReturnResponse();
                                                        productResponse.setOrderItemId(orderItem.getId());
                                                        productResponse.setProductName(product.getName());
                                                        productResponse.setProductVersionName(productVersion.getName());
                                                        productResponse.setColorName(productColorVersion.getColor());
                                                        productResponse.setImageUrl(imageMap.get(productVersion.getProductId()));
                                                        
                                                        // Lấy imeiOrSerial nếu có
                                                        if (orderItem.getProductItemId() != null) {
                                                                ProductItem productItem = productItemMap.get(orderItem.getProductItemId());
                                                                if (productItem != null) {
                                                                        productResponse.setImeiOrSerial(productItem.getImeiOrSerial());
                                                                }
                                                        }
                                                        
                                                        response.setProduct(productResponse);
                                                }
                                        }
                                }
                        }
                        
                        returnRequestResponseList.add(response);
                }
                
                return returnRequestResponseList;
        }
}
