package vn.hieu4tuoi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.hieu4tuoi.service.UserSecurityService;
import vn.hieu4tuoi.service.impl.JwtService;

import java.io.IOException;

@Component
@Slf4j(topic = "CUSTOMIZE-REQUEST-FILTER")
public class CustomizeRequestFitler extends OncePerRequestFilter {
    private JwtService jwtService;
    private UserSecurityService userSecurityService;

    @Autowired
    public CustomizeRequestFitler(JwtService jwtService, UserSecurityService userService) {
        this.jwtService = jwtService;
        this.userSecurityService = userService;
    }

    //kiểm tra quyền hạn của user từ request bên client gửi đến
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if(authHeader!=null && authHeader.startsWith("Bearer ")){// Bearer dc bên client điền vào trc token
            token = authHeader.substring(7);//cắt sau khoảng trắng bearer để lấy token
            try {
                username = jwtService.extractUsername(token);
            } catch (Exception e) {
                logger.error("JWT extraction failed", e);
            }
        }

        //neu token hop le thi cap quyen
        if(username!= null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails = userSecurityService.loadUserByUsername(username);
            if(jwtService.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authToken  = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);

    }
}
