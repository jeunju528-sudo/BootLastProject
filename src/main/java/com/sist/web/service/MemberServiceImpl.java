package com.sist.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sist.web.mapper.AuthorityMapper;
import com.sist.web.mapper.MemberMapper;
import com.sist.web.vo.AuthorityVO;
import com.sist.web.vo.MemberVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
	
	private final MemberMapper memberMapper;
	private final AuthorityMapper authorityMapper;

	@Override
	public MemberVO findByUsername(String username) {
		return memberMapper.findByUsername(username);
	}

	@Override
	public List<AuthorityVO> getAuthorityData(int member_id) {
		return authorityMapper.getAuthorityData(member_id);
	}

}
