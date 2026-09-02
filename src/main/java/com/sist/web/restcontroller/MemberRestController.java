package com.sist.web.restcontroller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sist.web.security.JWTAuthenticationProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberRestController {
	private final AuthenticationManager manager;
	private final JWTAuthenticationProvider provider;

	@RequestMapping("/member/login_ok")
	public ResponseEntity<?> login(@RequestParam("username") String username,
			@RequestParam("password") String password) {

		try {
			// username, password로 사용자 인증
			Authentication auth = manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			// 사용자 정보 가져오기 SecurityContextHolder 에 저장된 객체는 UserDetails 객체임
			UserDetails user = (UserDetails) auth.getPrincipal();
			System.out.println("사용자 정보 : " + user.getUsername());
			System.out.println("사용자 권한 갯수 : " + user.getAuthorities().size());
			// 사용자 권한 가져오기
			String role = user.getAuthorities().iterator().next().getAuthority();
			System.out.println("권한 : " + role);
			// JWT 생성
			String token = provider.createToken(user.getUsername(), role);
			System.out.println("토큰 : " + token);
			// 쿠키에 token 저장
			ResponseCookie cookie = ResponseCookie.from("accessToken", token).httpOnly(true).secure(false).path("/")
					.maxAge(3600).build();
			System.out.println("쿠키 : " + cookie.getValue());
			// 로그인 성공여부
			return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.SET_COOKIE, cookie.toString())
					.header(HttpHeaders.LOCATION, "/").build();

		} catch (BadCredentialsException e) { // 로그인 실패
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.header(HttpHeaders.LOCATION, "/member/login?error=true").build();
		} catch (AuthenticationException e) { // 인증 실패
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.header(HttpHeaders.LOCATION, "/member/login?error=true").build();
		} catch (Exception e) { // 서버오류
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.header(HttpHeaders.LOCATION, "/member/login?error=true").build();
		}

	}

	@GetMapping("/member/logout")
	public ResponseEntity<Void> logout() {
		// Cookie 삭제
		ResponseCookie cookie = ResponseCookie.from("accessToken", "").httpOnly(true).secure(false).path("/").maxAge(0)
				.build();
		return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString())
				.header(HttpHeaders.LOCATION, "/").build();

	}

}
