package vn.hieu4tuoi.service;

import java.util.List;

import vn.hieu4tuoi.dto.request.user.EmployeeRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.user.UserResponse;

public interface UserService {
   //get ds khách hàng
   PageResponse<List<UserResponse>> getCustomerList(String keyword, String sort, int page, int size);
   //get ds nhân viên
   PageResponse<List<UserResponse>> getEmployeeList(String keyword, String sort, int page, int size);

   //tạo tài khoản cho nhân viên
   String createEmployee(EmployeeRequest request);
   //xóa tài khoản nhân viên
   void deleteEmployee(String id);
   //lấy thông tin nhân viên
   UserResponse getEmployeeById(String id);
   //cập nhật thông tin nhân viên
   void updateEmployee(String id, EmployeeRequest request);
}
