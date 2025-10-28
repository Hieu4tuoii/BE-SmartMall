package vn.hieu4tuoi.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
public class JwtService {
    @Value("${jwt.secretKey}")
    private String SERECT;
    //Tạo JWT để kiểm soát đăng nhập và phân quyền
    //1.Tạo JwtService
    //2.Tạo JwtRespone
    //3. Tạo LoginRequest
    //4. Vieest hàm đăng nhập tại UserController


    //tao jwt dua tren ten dang nhap
    public String generateToken(String username, String role){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // add role to claims
        return createToken(claims, username);
    }

    //tao jwt voi cac claim da chon
    private String createToken (Map<String, Object> claims, String username){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))//time ban hanhjwt
                .setExpiration(new Date(System.currentTimeMillis() + 2L * 365 * 24 * 60 * 60 * 1000)) //2 năm
                .signWith( getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //lay serect key
    private Key getSignKey(){
        // Sử dụng Keys.secretKeyFor để tạo một khóa đủ mạnh
        //return Keys.secretKeyFor(SignatureAlgorithm.HS256);

        byte[] keyBytes = Decoders.BASE64.decode(SERECT);
        return  Keys.hmacShaKeyFor(keyBytes);
    }

    //trich xuat thong tin
    private Claims exctractALlClaims(String token){
        //phien ban jwt 0.12.6 tro len ko con parseClaimsJws
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //Trich xuat thong tin cho 1 claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction){
        final Claims claims = exctractALlClaims(token);
        return claimsTFunction.apply(claims);
    }

    //lay ra thoi gian het han tu jwt
    public Date extractExpiration(String token){
        return  extractClaim(token, Claims::getExpiration);
    }

    //lay ra username
    public String extractUsername(String token){
        return  extractClaim(token, Claims::getSubject);
    }

    //lay ra role
    public String extractRole(String token){
        final Claims claims = exctractALlClaims(token);
        return claims.get("role", String.class);
    }

    //kiem tra jwt da het han
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    //kiem tra tinh hop le
    public boolean validateToken(String token, UserDetails userDetails){
        final String userName = extractUsername(token);
        //kiem tra ten dang nhap tu token co giong voi ten dang nhap da dang nhap truoc do ko
        //va kiem tra xem da het han chua
        return (userName.equals(userDetails.getUsername())&&!isTokenExpired(token));
    }
}
