package com.sist.web.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sist.web.vo.AuthorityVO;
import com.sist.web.vo.MemberVO;

import lombok.RequiredArgsConstructor;

/**
 * Security에서 이해하는 사용자 객체로 반환하기 위한 클래스
 * */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

	private final MemberService memberService;
	
	// UserDetails : security가 요구하는 사용자 정보 표준 양식 객체
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		// 여기서 username은 사용자 식별값이지 사용자이름이 아닌 것에 주의하자!!
		MemberVO member = memberService.findByUsername(username);
		// MemberVO member = memberService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
		
		if(member == null) {
			throw new UsernameNotFoundException("사용자를 찾을 수가 없습니다 : " + username);
		}
		List<AuthorityVO> authList = memberService.getAuthorityData(member.getMember_id());
		
		// SimpleGrantedAuthority : security의 권한 객체
		List<SimpleGrantedAuthority> authorities = authList.stream()
														.map(auth-> new SimpleGrantedAuthority(auth.getAuthority()))
														.toList();
		
		/*
		 * 여기서 password 까지 넘겨줘서 login할 때 PasswordEncoder 따로 부르는 부분이 없어도 여기 담긴 패스워드를 보고 passwordEncoder.matcher 실행함
		 * */
		return User.builder()
					.username(member.getUsername())
					.password(member.getPassword())
					.authorities(authorities)
					.build();
	}
	
}
