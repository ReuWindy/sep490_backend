package com.fpt.sep490.configuration;

import com.fpt.sep490.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.allowOrigin.url}")
    String allowedOrigin;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    List<String> publicEndpoints = Arrays.asList("/register", "/login/loginRequest",
                                                 "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/order/customer/**","/ws/**", "/transaction/createTransaction",
                                                 "/user/**", "/employees/**", "/actuator/**", "/categories/all", "/products/customer/products", "/logout/**"
                                                ,"/ws/**");
    List<String> customerEndpoints = Arrays.asList("/products/customer/products", "/order/history/**","/order/customer/CreateOrder","/order/details/*");
    List<String> adminEndpoints = Arrays.asList("/suppliers/**", "/categories/**", "/batches/**", "/batchproducts/**","/products/**",
                                                "/WarehouseReceipt/**", "/employeerole/**","/ReceiptVoucher/**", "/ExpenseVoucher/**",
                                                "/news/", "/unitOfMeasures/**", "/productwarehouse/**", "/order/**",
                                                "/customer/**", "/contracts/**", "/warehouses/**", "/price/**", "/employees/**",
                                                "/transaction/**","/inventory/**", "/finishedProduct/**", "/productionOrder/**", "/user-activities/**");
    List<String> warehouseManager = Arrays.asList("/categories/**", "/products/admin/products", "/products/admin/createProduct", "/products/import/preview", "/products/import/excel",
            "/products/import/previewFromProduction", "/products/export/preview", "/products/admin/products", "/products/admin/order/products", "/products/", "/products/admin/createProduct",
            "/products/admin/updateProduct", "/products/delete/*","/products/", "/products/*", "/products/enable/*", "/order/admin/orders", "/customer/",
            "/order/details/*", "/order/admin/UpdateOrderDetail/*", "/order/admin/UpdateOrder/*", "/WarehouseReceipt/", "/order/admin/CreateOrder", "/suppliers/all", "/warehouses/all",
            "/batchproducts/batchCode/*", "/batchproducts/update/*", "/batchproducts/productId/*", "/batches/batchCode/*", "/inventory/getAll", "/inventory/createInventory", "/inventory/delete/*", "/inventory/getInventory/*", "/productwarehouse/getAllProductsWarehouse",
            "/productionOrder/getAll", "/productionOrder/getById/*", "/productionOrder/getWithFilter", "/productionOrder/createProductionOrder", "/productionOrder/update/*", "/productionOrder/finishProduction/*",
            "/productwarehouse/getAllIngredients");

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(httpRequestsMatcher -> httpRequestsMatcher
                        .requestMatchers(publicEndpoints.toArray(new String[0])).permitAll()
                        .requestMatchers(customerEndpoints.toArray(new String[0])).hasAnyRole("CUSTOMER", "ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(warehouseManager.toArray(new String[0])).hasAnyRole("WAREHOUSE_MANAGER","ADMIN")
                        .requestMatchers(adminEndpoints.toArray(new String[0])).hasRole("ADMIN"))

                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(Collections.singletonList(allowedOrigin));
                    corsConfiguration.setAllowedMethods(List.of(
                            RequestMethod.GET.name(),
                            RequestMethod.POST.name(),
                            RequestMethod.PUT.name(),
                            RequestMethod.DELETE.name()
                    ));
                    corsConfiguration.setAllowCredentials(true);
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setExposedHeaders(List.of("Authorization"));
                    corsConfiguration.setMaxAge(Duration.of(1L, ChronoUnit.HOURS));
                    return corsConfiguration;
                }))
                .csrf(csrf -> csrf
                        .csrfTokenRequestHandler(requestHandler)
                        .ignoringRequestMatchers("/**")
                        .ignoringRequestMatchers("/ws/info")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}