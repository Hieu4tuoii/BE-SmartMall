package vn.hieu4tuoi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vn.hieu4tuoi.dto.request.user.EmployeeRequest;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.service.UserService;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller")
@Slf4j(topic = "USER-CONTROLLER")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/customer")
    @Operation(summary = "Lấy danh sách khách hàng")
    public ResponseData<?> getCustomerList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách khách hàng thành công",
                userService.getCustomerList(keyword, sort, page, size));
    }

    @GetMapping("/employee")
    @Operation(summary = "Lấy danh sách nhân viên")
    public ResponseData<?> getEmployeeList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách nhân viên thành công",
                userService.getEmployeeList(keyword, sort, page, size));
    }

    @PostMapping("/employee")
    @Operation(summary = "Tạo tài khoản nhân viên")
    public ResponseData<?> createEmployee(@RequestBody EmployeeRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Tạo tài khoản nhân viên thành công",
                userService.createEmployee(request));
    }

    @DeleteMapping("/employee/{id}")
    @Operation(summary = "Xóa tài khoản nhân viên")
    public ResponseData<?> deleteEmployee(@PathVariable String id) {
        userService.deleteEmployee(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Xóa tài khoản nhân viên thành công");
    }

    @GetMapping("/employee/{id}")
    @Operation(summary = "Lấy thông tin nhân viên")
    public ResponseData<?> getEmployeeById(@PathVariable String id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy thông tin nhân viên thành công", userService.getEmployeeById(id));
    }

    @PutMapping("/employee/{id}")
    @Operation(summary = "Cập nhật thông tin nhân viên")
    public ResponseData<?> updateEmployee(@PathVariable String id, @RequestBody EmployeeRequest request) {
        userService.updateEmployee(id, request);
        return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật thông tin nhân viên thành công");
    }
}
