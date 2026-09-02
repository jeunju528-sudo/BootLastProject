package com.sist.web.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sist.web.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 키 인증 */
public class JWTAuthenticationFilter extends OncePerRequestFilter{
	private final CustomUserDetailsService uds;
	private final JWTAuthenticationProvider provider;
	
	public JWTAuthenticationFilter(CustomUserDetailsService uds, JWTAuthenticationProvider provider) {
		this.uds = uds;
		this.provider = provider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String token = null;
		String header = request.getHeader("Authorization"); // 인증 토큰 사용 시 Http Header에 담겨서 들어오는 값 {"Authorization":"Bearer ezuadcdsersdfsdf..."}
		
		// 프론트엔드에서 넘어온 인증값이 있다면
		if(header!=null && header.startsWith("Bearer ")) {
			token = header.substring(7);
		}
		
		// 헤더에서 넘어오는 토큰이 없지만 쿠키가 있다면
		if(token == null && request.getCookies()!=null) {
			for(Cookie cookie : request.getCookies()) {
				// cookie이름이 accessToken이면
				if("accessToken".equals(cookie.getName())) {
					token = cookie.getValue();
					break;
				}
			}
		}
		
		// 토큰이 있으면 토큰 검증을 하고 토큰이 정상이면
		if(token != null && provider.validate(token)) {
			// 토큰에서 사용자id 가지고 옴
			String username = provider.getUsername(token);
			// 가져온 사용자 id를 db에서 체크를 함
			UserDetails user = uds.loadUserByUsername(username);
			// 사용자 권한 정보를 가지고 옴
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			// security에 사용자 정보 저장
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		
		filterChain.doFilter(request, response);
	}
}
