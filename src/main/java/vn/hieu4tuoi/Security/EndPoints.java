package vn.hieu4tuoi.Security;


import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

public class EndPoints {

    //pthuc get dung chung cho tat ca
    public static final String[] PUBLIC_GET_ENDPOINTS = {
            // Swagger UI endpoints
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/category/**",
            "/brand/**",
            "/upload/images/**",
            "/banner/public/**",
            "/product/public/**",
            "/review/public/**",
            "/cart/count",
    };
    public static final String[] PUBLIC_POST_ENDPOINTS = {
            "/api/auth/**",
    };

    public static final String[] PUBLIC_PUT_ENDPOINTS = {
       
    };

    public static final String[] CUSTOMER_PUT_ENDPOINTS = {
        "/cart/update",
    };

    public static final String[] CUSTOMER_GET_ENDPOINTS = {
        "/order/list/current-user",
    };

    public static final String[] ADMIN_GET_ENDPOINTS = {
        "/category/**",
        "/brand/**",
        "/product/**",
        "/user/**",
        "/order/**",
        
    };

    public static final String[] ADMIN_POST_ENDPOINTS = {
        "/category/**",
        "/brand/**",
        "/product/**",
        "/user/**",
        "/upload/images",
        "/banner/**",
    };
    public static final String[] ADMIN_PUT_ENDPOINTS = {
        "/category/**",
        "/brand/**",
        "/product/**",
        "/user/**",
        "/banner/**",
    };
    public static final String[] ADMIN_DELTE_ENDPOINTS = {
        "/category/**",
        "/brand/**",
        "/product/**",
        "/user/**",
        "/banner/**",
    };

}
