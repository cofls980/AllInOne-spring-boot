package com.hongik.pcrc.allinone.security.config;

import com.hongik.pcrc.allinone.security.handler.AuthenticationEntryPointHandler;
import com.hongik.pcrc.allinone.security.handler.WebAccessDeniedHandler;
import com.hongik.pcrc.allinone.security.jwt.JwtAuthenticationFilter;
import com.hongik.pcrc.allinone.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableWebMvc
public class SecurityConfig implements WebMvcConfigurer {

    private final JwtProvider jwtProvider;
    private final WebAccessDeniedHandler webAccessDeniedHandler;
    private final AuthenticationEntryPointHandler authenticationEntryPointHandler;

    @Value("${external.frontend}")
    private String frontend;
    @Value("${external.backend}")
    private String backend;
    @Value("${external.localFront}")
    private String localFront;
    @Value("${external.localBack}")
    private String localBack;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String v = "/v2";
        String[] users = {v + "/users"}; //PUT
        String[] boards = {v + "/boards", v + "/boards/{board_id}"}; //GET
        String[] uri = {v + "/users/signup", v + "/users/login", "/api/*", "/v3/api-docs",
                v + "/email/*", v + "/email", v + "/security/reissue", "/swagger*/**",
                "/chat/**/*", "/chat"
        };

        http.cors().configurationSource(corsConfigurationSource());
        http.httpBasic().disable()
                .csrf().disable() // csrf 필요없음
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt로 인증하므로 세션 Stateless 처리
                .and()
                    .authorizeRequests()
                    //.anyRequest().permitAll()
                    .antMatchers(HttpMethod.PUT, users).permitAll()
                    .antMatchers(HttpMethod.GET, boards).permitAll()
                    .antMatchers(uri).permitAll()
                    .anyRequest().authenticated()
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPointHandler)
                    .accessDeniedHandler(webAccessDeniedHandler)
                .and()
                    .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin(frontend);
        configuration.addAllowedOrigin(backend);
        configuration.addAllowedOrigin(localFront);
        configuration.addAllowedOrigin(localBack);
        configuration.addAllowedHeader(localFront);
        configuration.addAllowedHeader(frontend);
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {   // 기본 컨버터를 유지관리
        converters.removeIf(v->v.getSupportedMediaTypes().contains(MediaType.APPLICATION_JSON));  // 기존 json용 컨버터 제거
        converters.add(new MappingJackson2HttpMessageConverter());  // 새로 json 컨버터 추가. 필요시 커스텀 컨버터 bean 사용
    }

}
