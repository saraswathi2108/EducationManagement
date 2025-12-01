//package com.project.student.education.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//import java.util.Arrays;
//@Configuration
//public class GlobalCorsConfig {
//
//    @Bean
//    public CorsFilter corsFilter() {
//
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//
//        config.setAllowedOrigins(Arrays.asList(
//                "http://localhost:3000",
//                "http://localhost:19006",
//                "http://localhost:19000",
//                "http://192.168.0.112:3000",
//                "http://192.168.0.112:*"
//        ));
//
//        config.addAllowedOriginPattern("http://192.168.*.*:*");
//
//        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//
//        config.setAllowedHeaders(Arrays.asList(
//                "Authorization",
//                "Content-Type",
//                "Accept",
//                "Cache-Control",
//                "X-Requested-With",
//                "X-Event-Source"
//        ));
//
//        config.setExposedHeaders(Arrays.asList(
//                "Authorization",
//                "Content-Type",
//                "Cache-Control",
//                "X-Event-Source"
//        ));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return new CorsFilter(source);
//    }
//}
