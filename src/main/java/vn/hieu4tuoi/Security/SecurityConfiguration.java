package vn.hieu4tuoi.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import vn.hieu4tuoi.config.CustomizeRequestFitler;
import vn.hieu4tuoi.service.UserSecurityService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfiguration {

    @Autowired
    private CustomizeRequestFitler customizeRequestFitler;

    @Value("${fe.domains}")
    private String feDomains;

    public List<String> getAllowedOrigins() {
        // Chuyển chuỗi thành danh sách, loại bỏ khoảng trắng
        return Arrays.stream(feDomains.split(","))
                .map(String::trim) // Cắt khoảng trắng thừa ở đầu và cuối mỗi item
                .collect(Collectors.toList());
    }

    //CẤU HÌNH PHÂN QUYỀN
    //1.Tạo userSecurityService extends UserDetailService
    //2.Hiện thực hàm loadUser
    //3.Viết hàm authenticationProvider và securityFilterChain
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //cài đặt bộ mã hóa mật khẩu và tải userDetailService để phân quyền cho người dùng đang đăng nhập
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserSecurityService userService){
        DaoAuthenticationProvider dap = new DaoAuthenticationProvider();
        dap.setUserDetailsService(userService);
        dap.setPasswordEncoder(passwordEncoder());
        return dap;
    }

    //chỉ định quyền truy cập vào các endpoint cho từng role
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(
                configurer->configurer
                        .requestMatchers(HttpMethod.GET, EndPoints.PUBLIC_GET_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, EndPoints.ADMIN_GET_ENDPOINTS).hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, EndPoints.PUBLIC_POST_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.PUT, EndPoints.PUBLIC_PUT_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, EndPoints.CUSTOMER_GET_ENDPOINTS).hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, EndPoints.CUSTOMER_PUT_ENDPOINTS).hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, EndPoints.ADMIN_POST_ENDPOINTS).hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, EndPoints.ADMIN_PUT_ENDPOINTS).hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, EndPoints.ADMIN_DELTE_ENDPOINTS).hasAnyAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
        );

        //cau hinh cors cho phep fe truy cap
        http.cors(cors -> {
            cors.configurationSource(request -> {
                CorsConfiguration corsConfiguration = new CorsConfiguration();

                // Lấy danh sách các domain và IP
                List<String> allowedOrigins = getAllowedOrigins();
                allowedOrigins.forEach(corsConfiguration::addAllowedOrigin);

                // Cấu hình CORS khác
                corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                corsConfiguration.addAllowedHeader("*");
                corsConfiguration.setAllowCredentials(true); // Cho phép cookie nếu cần
                return corsConfiguration;
            });
        });

        //thêm bộ lọc jwt đảm bảo rằng chỉ các yêu cầu đã được xác thực JWT mới được xử lý tiếp
        http.addFilterBefore( customizeRequestFitler, UsernamePasswordAuthenticationFilter.class);

        //ko giữ trạng thái session(vì sdung jwt)
        http.sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf->csrf.disable());
        return http.build();
    }

    //get authenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }


}

