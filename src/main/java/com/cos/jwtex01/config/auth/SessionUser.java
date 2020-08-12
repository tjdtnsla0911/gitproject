package com.cos.jwtex01.config.auth;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionUser {
	//User오브젝트로 담으면 패스워드가 담기기때문에 만듬
	private long id;
	private String username;
	private List<String> roles;
}
