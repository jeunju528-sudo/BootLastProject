package com.sist.web.vo;

import java.sql.Date;

import lombok.Data;

@Data
public class MemberVO {
	private int member_id;  
	private String username;     
	private String password;     
	private String name;         
	private String sex;          
	private Date regdate;      
	private Date birthdate;    
	private String phone;        
	private String post;         
	private String addr1;        
	private String addr2;        
	private String email;        
	private String profile_desc; 
	private String profile_image;
	private int enabled;      
}
