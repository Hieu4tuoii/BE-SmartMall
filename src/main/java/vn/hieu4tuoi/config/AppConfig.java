// package vn.hieu4tuoi.config;

// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class AppConfig {

// //    private final CustomizeRequestFitler requestFilter;
// //    private final UserServiceDetail userServiceDetail;

// //    @Bean
// //    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
// //        http.csrf(AbstractHttpConfigurer::disable)
// //                .authorizeHttpRequests(request -> request.requestMatchers("/auth/**").permitAll()
// //                        .anyRequest().authenticated())
// //                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
// //                .authenticationProvider(authenticationProvider()).addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);
// //        return http.build();
// //    }
// //
// //    @Bean
// //    public WebSecurityCustomizer ignoreResources() {
// //        return webSecurity -> webSecurity
// //                .ignoring()
// //                .requestMatchers("/actuator/**", "/v3/**", "/webjars/**", "/swagger-ui*/*swagger-initializer.js", "/swagger-ui*/**", "/favicon.ico");
// //    }
// //
// //    @Bean
// //    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
// //        return config.getAuthenticationManager();
// //    }
// //
// //    @Bean
// //   public AuthenticationProvider authenticationProvider() {
// //       DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
// //       authProvider.setPasswordEncoder(passwordEncoder());
// //       authProvider.setUserDetailsService(userServiceDetail.userDetailsService());
// //       return authProvider;
// //    }
// //
// //    @Bean
// //    public PasswordEncoder passwordEncoder() {
// //        return new BCryptPasswordEncoder();
// //    }
// }
