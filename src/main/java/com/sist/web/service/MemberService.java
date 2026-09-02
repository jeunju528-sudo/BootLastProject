package com.sist.web.service;

import java.util.List;

import com.sist.web.vo.AuthorityVO;
import com.sist.web.vo.MemberVO;

public interface MemberService {
	public MemberVO findByUsername(String username);
	public List<AuthorityVO> getAuthorityData(int member_id);
}
