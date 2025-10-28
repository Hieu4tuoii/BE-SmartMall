package vn.hieu4tuoi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import vn.hieu4tuoi.Security.JwtRespone;
import vn.hieu4tuoi.common.StepActive;
import vn.hieu4tuoi.dto.request.auth.ConfirmOtpRequest;
import vn.hieu4tuoi.dto.request.auth.RegisterInformationRequest;
import vn.hieu4tuoi.dto.request.auth.SignInRequest;
import vn.hieu4tuoi.dto.request.auth.SignUpRequest;
import vn.hieu4tuoi.exception.BadRequestException;
import vn.hieu4tuoi.exception.InvalidDataException;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.exception.UnauthorizedException;
import vn.hieu4tuoi.model.Authorities;
import vn.hieu4tuoi.model.User;
import vn.hieu4tuoi.repository.AuthoritiesRepository;
import vn.hieu4tuoi.repository.UserRepository;
import vn.hieu4tuoi.service.AuthService;
import vn.hieu4tuoi.mapper.UserMapper;
import vn.hieu4tuoi.service.MailService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final MailService mailService;
    private final AuthoritiesRepository authoritiesRepository;

    @Override
    public JwtRespone login(SignInRequest request) {
        //xac thuc nguoi dung bang ten dang nhap va mat khau
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        var user = userRepository.findByEmailAndIsDeletedFalse(request.getEmail());

        List<String> roles = user.getAuthorities().stream()
                .map(Authorities::getAuthoritiesID)
                .toList();
        String jwt = jwtService.generateToken(request.getEmail(), roles.get(0));
        JwtRespone jwtRespone = userMapper.UserToJwtResponse(user);
        jwtRespone.setJwt(jwt);
        return jwtRespone;
    }

    @Override
    public String register(SignUpRequest request) throws IOException {
        // Kiểm tra email đã tồn tại chưa
        User existingUser = userRepository.findByEmailAndIsDeletedFalse(request.getEmail());
        if (existingUser != null && existingUser.getStepActive()!=StepActive.OTP) {
            throw new InvalidDataException("Email đã được sử dụng");
        }else if(existingUser == null){
            existingUser = new User();
        }

        // Kiểm tra repass vs pass có giống nhau không
        if (!request.getPassword().equals(request.getRePassword())) {
            throw new BadRequestException("Mật khẩu không khớp");
        }

        existingUser.setEmail(request.getEmail());
        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        existingUser.setStepActive(StepActive.OTP);

        // Tạo OTP ngẫu nhiên gồm 6 chữ số
        String otp = String.format("%06d", (int) (Math.random() * 1000000));
        existingUser.setOTP(otp);

        //gui mail
        // mailService.emailVerification(existingUser.getEmail(), otp);

        try {
            String displayName = existingUser.getFullName() != null ? existingUser.getFullName() : "Khách hàng";
            mailService.sendOtp(existingUser.getEmail(), otp, displayName);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        userRepository.save(existingUser);
        return existingUser.getId();
    }

    @Override
    public void confirmOTP(ConfirmOtpRequest request) {
        User user = userRepository.findByIdAndIsDeleted(request.getId(), false);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng không tồn tại");
        }
        if (user.getStepActive() != StepActive.OTP) {
            throw new BadRequestException("Người dùng đã hoàn thành đăng ký");
        }
        if(user.getOTP() == null || !user.getOTP().equals(request.getOtp())){
            throw new BadRequestException("OTP không đúng");
        }
        user.setOTP(null);
        user.setStepActive(StepActive.UPDATE_INFO);
        userRepository.save(user);
    }

    @Override
    public void registerInformation(RegisterInformationRequest request) {
        User user = userRepository.findByIdAndIsDeleted(request.getId(), false);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng không tồn tại");
        }
        if (user.getStepActive() != StepActive.UPDATE_INFO) {
            throw new BadRequestException("Người dùng chưa xác thực OTP hoặc đã hoàn thành đăng ký");
        }

        // Cập nhật thông tin người dùng
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setStepActive(StepActive.ACTIVE);
        user.setStatus(vn.hieu4tuoi.common.UserStatus.ACTIVE);

        //set role
        Authorities customerRole = authoritiesRepository.findById("ROLE_CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Vai trò CUSTOMER không tồn tại"));
        
        List<Authorities> authoritiesList = new ArrayList<>();
        authoritiesList.add(customerRole);
        user.setAuthorities(authoritiesList);
        // customerRole.getUserList().add(user);

        userRepository.save(user);
    }
}
