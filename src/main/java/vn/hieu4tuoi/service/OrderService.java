package vn.hieu4tuoi.service;

import java.util.List;

import vn.hieu4tuoi.common.OrderStatus;
import vn.hieu4tuoi.common.WarrantyStatus;
import vn.hieu4tuoi.common.ReturnRequestStatus;
import vn.hieu4tuoi.dto.request.order.OrderByAIRequest;
import vn.hieu4tuoi.dto.request.order.OrderRequest;
import vn.hieu4tuoi.dto.request.order.ReturnRequestRequest;
import vn.hieu4tuoi.dto.request.order.UpdateOrderStatusRequest;
import vn.hieu4tuoi.dto.request.order.UpdateWarrantyStatusRequest;
import vn.hieu4tuoi.dto.request.order.UpdateReturnRequestStatusRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.order.OrderAdminResponse;
import vn.hieu4tuoi.dto.respone.order.OrderDetailResponse;
import vn.hieu4tuoi.dto.respone.order.OrderResponse;
import vn.hieu4tuoi.dto.respone.order.WarrantyClaimResponse;
import vn.hieu4tuoi.dto.respone.order.ReturnRequestResponse;

public interface OrderService {
    String createOrder(OrderRequest request);
    
    /**
     * Tạo đơn hàng thông qua AI Chatbot
     * @param request thông tin đặt hàng từ AI Tool
     * @return thông báo kết quả đặt hàng
     */
    String createOrderByAI(OrderByAIRequest request);
    
    /**
     * Tạo đơn hàng trực tiếp từ frontend (mua ngay)
     * @param request thông tin đặt hàng trực tiếp
     * @return ID của đơn hàng đã tạo
     * @throws UnauthorizedException nếu user chưa đăng nhập
     * @throws BadRequestException nếu có lỗi validation hoặc logic
     * @throws ResourceNotFoundException nếu không tìm thấy sản phẩm
     */
    String createOrderDirect(OrderByAIRequest request);
    PageResponse<List<OrderAdminResponse>> getOrderList(int page, int size, String sort, String keyword, OrderStatus status);

    List<OrderResponse> getOrderListByCurrentUser();

    OrderDetailResponse getOrderDetail(String id);

    void updateOrderStatus(String id, UpdateOrderStatusRequest request);

    void createReturnRequest(ReturnRequestRequest request);

    /**
     * Lấy danh sách yêu cầu bảo hành của user hiện tại
     * @return danh sách yêu cầu bảo hành
     */
    List<WarrantyClaimResponse> getWarrantyClaimListByCurrentUser();

    /**
     * Lấy danh sách yêu cầu trả hàng của user hiện tại
     * @return danh sách yêu cầu trả hàng
     */
    List<ReturnRequestResponse> getReturnRequestListByCurrentUser();

    /**
     * Lấy danh sách yêu cầu bảo hành cho admin với phân trang và filter theo status
     * @param page số trang
     * @param size kích thước trang
     * @param sort sắp xếp
     * @param status trạng thái (có thể null)
     * @return danh sách yêu cầu bảo hành
     */
    PageResponse<List<WarrantyClaimResponse>> getWarrantyClaimListForAdmin(int page, int size, String sort, WarrantyStatus status);

    /**
     * Lấy danh sách yêu cầu trả hàng cho admin với phân trang và filter theo status
     * @param page số trang
     * @param size kích thước trang
     * @param sort sắp xếp
     * @param status trạng thái (có thể null)
     * @return danh sách yêu cầu trả hàng
     */
    PageResponse<List<ReturnRequestResponse>> getReturnRequestListForAdmin(int page, int size, String sort, ReturnRequestStatus status);

    /**
     * Cập nhật trạng thái bảo hành (admin)
     * @param id ID yêu cầu bảo hành
     * @param request thông tin cập nhật
     */
    void updateWarrantyStatus(String id, UpdateWarrantyStatusRequest request);

    /**
     * Cập nhật trạng thái trả hàng (admin)
     * @param id ID yêu cầu trả hàng
     * @param request thông tin cập nhật
     */
    void updateReturnRequestStatus(String id, UpdateReturnRequestStatusRequest request);

    /**
     * Lấy chi tiết yêu cầu bảo hành (admin)
     * @param id ID yêu cầu bảo hành
     * @return chi tiết yêu cầu bảo hành
     */
    WarrantyClaimResponse getWarrantyClaimDetail(String id);

    /**
     * Lấy chi tiết yêu cầu trả hàng (admin)
     * @param id ID yêu cầu trả hàng
     * @return chi tiết yêu cầu trả hàng
     */
    ReturnRequestResponse getReturnRequestDetail(String id);

    
}
