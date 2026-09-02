package com.sist.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sist.web.security.JWTAuthenticationFilter;
import com.sist.web.security.JWTAuthenticationProvider;
import com.sist.web.service.CustomUserDetailsService;

/*
 * 1. 서버가 뜰 때 딱 한 번 실행되어서 스프링 빈에 등록해둠
 * 
 * */
@Configuration
@EnableWebSecurity
public class JwtSecurityConfig {
	
	// 비밀번호 암호화
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// JWT Filter 등록
	@Bean
	public JWTAuthenticationFilter jwtAuthenticationFilter(CustomUserDetailsService uds, JWTAuthenticationProvider provider) {
		return new JWTAuthenticationFilter(uds, provider);
	}
	
	// 인가 관리자
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}
	
	/* 개발자가 SecurityFilterChain을 직접 정의하면 기본 시큐리티체인 사용 안하게 됨! */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, JWTAuthenticationFilter jwtAuthenticationFilter) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.formLogin(form -> form.disable()) // 기본 로그인 폼 사용 안함
			.authorizeHttpRequests(auth -> auth.requestMatchers("/","/login","/member").permitAll()
												.requestMatchers("/admin").hasRole("ADMIN")
												.anyRequest().permitAll())
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
