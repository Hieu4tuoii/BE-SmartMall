package vn.hieu4tuoi.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.hieu4tuoi.Security.SecurityUtils;
import vn.hieu4tuoi.common.StepActive;
import vn.hieu4tuoi.common.UserStatus;
import vn.hieu4tuoi.dto.request.user.CustomerUpdateRequest;
import vn.hieu4tuoi.dto.request.user.EmployeeRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.user.UserResponse;
import vn.hieu4tuoi.mapper.UserMapper;
import vn.hieu4tuoi.exception.InvalidDataException;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.model.Authorities;
import vn.hieu4tuoi.model.User;
import vn.hieu4tuoi.repository.AuthoritiesRepository;
import vn.hieu4tuoi.repository.UserRepository;
import vn.hieu4tuoi.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthoritiesRepository authoritiesRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public PageResponse<List<UserResponse>> getCustomerList(String keyword, String sort, int page, int size) {
        Authorities customerAuthority = authoritiesRepository.findById("ROLE_CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Vai trò CUSTOMER không tồn tại"));
        return searchUserByRole(customerAuthority, keyword, sort, page, size);
    }

    @Override
    public PageResponse<List<UserResponse>> getEmployeeList(String keyword, String sort, int page, int size) {
        Authorities employeeAuthority = authoritiesRepository.findById("ROLE_EMPLOYEE")
                .orElseThrow(() -> new ResourceNotFoundException("Vai trò EMPLOYEE không tồn tại"));
        return searchUserByRole(employeeAuthority, keyword, sort, page, size);
    }
    
    private PageResponse<List<UserResponse>> searchUserByRole(Authorities role, String keyword, String sort, int page, int size) {
        // Xử lý sắp xếp
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "modifiedAt"); // Mặc định sắp xếp theo modifiedAt desc
        if (StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                order = matcher.group(3).equalsIgnoreCase("asc")
                        ? new Sort.Order(Sort.Direction.ASC, columnName)
                        : new Sort.Order(Sort.Direction.DESC, columnName);
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        // Tìm kiếm theo keyword hoặc lấy tất cả
        if (StringUtils.hasLength(keyword)) {
            keyword = "%" + keyword.toLowerCase() + "%";
        } else {
            keyword = "%%";
        }
        
        Page<User> entityPage = userRepository.searchCustomerByKeyword(role, keyword, pageable);

        // Map sang UserResponse
        List<UserResponse> userList = entityPage.stream()
                .map(user -> userMapper.entityToResponse(user))
                .toList();

        return PageResponse.<List<UserResponse>>builder()
                .pageNo(page)
                .pageSize(size)
                .totalPage(entityPage.getTotalPages())
                .items(userList)
                .build();
    }

    @Override
    public String createEmployee(EmployeeRequest request) {
        // Kiểm tra email đã tồn tại (chỉ tính user chưa bị xóa)
        User existingUser = userRepository.findByEmailAndIsDeletedFalse(request.getEmail());
        if (existingUser != null) {
            throw new InvalidDataException("Email đã được sử dụng");
        }

        User user = userMapper.employeeRequestToEntity(request);
        if (StringUtils.hasText(request.getPassword())) {
            // Mã hóa mật khẩu nhân viên trước khi lưu
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setStepActive(StepActive.ACTIVE);
        user.setStatus(UserStatus.ACTIVE);
        //set role
        Authorities customerRole = authoritiesRepository.findById("ROLE_EMPLOYEE")
                .orElseThrow(() -> new ResourceNotFoundException("Vai trò EMPLOYEE không tồn tại"));
        
        List<Authorities> authoritiesList = new ArrayList<>();
        authoritiesList.add(customerRole);
        user.setAuthorities(authoritiesList);
        user = userRepository.save(user);
        return user.getId();
    }

    @Override
    public void deleteEmployee(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản nhân viên không tồn tại"));
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Override
    public UserResponse getEmployeeById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản nhân viên không tồn tại"));
        return userMapper.entityToResponse(user);
    }

    @Override
    public void updateEmployee(String id, EmployeeRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản nhân viên không tồn tại"));
        userMapper.updateUser(request, user);

        if (StringUtils.hasText(request.getPassword())) {
            // Mã hóa mật khẩu khi cập nhật thông tin nhân viên (nếu có đổi mật khẩu)
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        //set role
        Authorities employeeRole = authoritiesRepository.findById("ROLE_EMPLOYEE")
                .orElseThrow(() -> new ResourceNotFoundException("Vai trò EMPLOYEE không tồn tại"));
        List<Authorities> authoritiesList = new ArrayList<>();
        authoritiesList.add(employeeRole);
        user.setAuthorities(authoritiesList);

        userRepository.save(user);
    }

    @Override
    public UserResponse getCurrentCustomer() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin tài khoản khách hàng");
        }

        User user = userRepository.findByIdAndIsDeleted(currentUserId, false);
        if (user == null) {
            throw new ResourceNotFoundException("Tài khoản khách hàng không tồn tại");
        }

        return userMapper.entityToResponse(user);
    }

    @Override
    public void updateCurrentCustomer(CustomerUpdateRequest request) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin tài khoản khách hàng");
        }

        User user = userRepository.findByIdAndIsDeleted(currentUserId, false);
        if (user == null) {
            throw new ResourceNotFoundException("Tài khoản khách hàng không tồn tại");
        }

        userMapper.updateCustomer(request, user);
        userRepository.save(user);
    }
}
