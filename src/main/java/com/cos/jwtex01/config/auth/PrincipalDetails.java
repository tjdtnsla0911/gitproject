package com.cos.jwtex01.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cos.jwtex01.model.User;

public class PrincipalDetails implements UserDetails{
	//여기~
	private User user;
	public PrincipalDetails(User user) {
		System.out.println("config.auth.PrincipalDetails의 PrincipalDetails의 user = "+user);
		this.user = user;
	}
	public User getUser() {
		System.out.println("config.auth.PrincipalDetails의 getUser의 user = "+user);
		return user;
	}
	//까지 만들고 오버라이드
	//setter는 세션할거면 필요하지만
	//Userdetails가 Security~
	//로그인을 직접시키던가 UserDetails값의 객체를 수정하면 됨

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		System.out.println("config.auth.PrincipalDetails의 getAuthorities에 왔습니다 " );
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		user.getRoleList().forEach(r ->{

			authorities.add(()->{return r;});
			System.out.println("config.auth.PrincipalDetails의 getAuthorities의 authorities 하는중 =  "+authorities );
		});
		System.out.println("config.auth.PrincipalDetails의 getAuthorities의 authorities의 완료 =  "+authorities );
		return authorities;
	}

	@Override
	public String getPassword() {
		System.out.println("config.auth.PrincipalDetails의 getPassword의 user.getPassword  =  "+user.getPassword() );
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		System.out.println("config.auth.PrincipalDetails의 getUsername의 user.getUsername  =  "+user.getUsername() );
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		System.out.println("config.auth.PrincipalDetails의 isAccountNonExpired에 왔습니다" );
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		System.out.println("config.auth.PrincipalDetails의 isAccountNonExpired에 왔습니다" );
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
