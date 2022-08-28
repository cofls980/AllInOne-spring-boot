package com.hongik.pcrc.allinone.security.config;

import com.hongik.pcrc.allinone.security.handler.AuthenticationEntryPointHandler;
import com.hongik.pcrc.allinone.security.handler.WebAccessDeniedHandler;
import com.hongik.pcrc.allinone.security.jwt.JwtAuthenticationFilter;
import com.hongik.pcrc.allinone.security.jwt.JwtProvider;
import com.hongik.pcrc.allinone.security.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtProvider jwtProvider;
    private final WebAccessDeniedHandler webAccessDeniedHandler;
    private final AuthenticationEntryPointHandler authenticationEntryPointHandler;
    private final CustomUserDetailService customUserDetailService;

    // AuthenticationManagerBuilder를 통해 PasswordEncoder 구현체 지정
    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(customUserDetailService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String v = "/v2";
        String[] users = {v + "/users"}; //PUT
        String[] boards = {v + "/boards", v + "/boards/{board_id}"}; //GET
        //조회수?
        String[] uri = {v + "/users/signup", v + "/users/login",
                v + "/email/*", v + "/security/reissue"};

        http.httpBasic().disable()
                .csrf().disable() // csrf 필요없음
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt로 인증하므로 세션 Stateless 처리
                .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.PUT, users).permitAll()
                    .antMatchers(HttpMethod.GET, boards).permitAll()
                    .antMatchers(uri).permitAll() // logout은 auth에서 빼야함
                    .anyRequest().authenticated()
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPointHandler)
                    .accessDeniedHandler(webAccessDeniedHandler)
                .and()
                    .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

    }
}
