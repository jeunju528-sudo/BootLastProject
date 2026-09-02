package com.sist.web.security;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/** 토큰 생성 및 토큰 검증하는 클래스 */
@Component
public class JWTAuthenticationProvider {
	
	// 자동 키 생성
	/*
	public String createSecretKey() {
		SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
		String secretKey = Encoders.BASE64.encode(key.getEncoded());
		return secretKey;
	}
	*/
	
	private final String SECRET_KEY = "one-secret-key-two-secret-key-three-secret-key-four-secret-key";
	
	// Token 생성
	public String createToken(String username, String role) {
		
		return Jwts.builder()
				.setSubject(username)
				.claim("role", role)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis()+360000))
				.signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
				.compact();
	}
	
	// 토큰에서 사용자 ID 추출
	public String getUsername(String token) {
		return Jwts.parserBuilder()
					.setSigningKey(SECRET_KEY.getBytes())
					.build()
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
	}
	
	// 위조 확인
	public boolean validate(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(SECRET_KEY.getBytes())
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
}
