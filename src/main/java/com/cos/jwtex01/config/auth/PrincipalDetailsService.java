package com.cos.jwtex01.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.jwtex01.model.User;
import com.cos.jwtex01.repository.UserRepository;

@Service
public class PrincipalDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("받은 username = "+username);
		System.out.println("config.auth.PrincipalDetailsService : 진입");
		User user = userRepository.findByUsername(username);//여기서 select 문으로 출력하는듯합니다.
		System.out.println("config.auth.PrincipalDetailsService우ㅏ user = "+user);
		//여기서 못찾으면 null이 아니라 throws로 Exception이 발생되는데 Global로 잡아서 해도 된다.

		if(user != null) {
			System.out.println("config.auth.PrincipalDetailsService우의 if문에온  user = "+user);
			//session.setAttribute("loginUser",user);
			//이때 내부적으로 로그인이 성공되어서 Authentication가 생성됨
			return new PrincipalDetails(user);
		}
		System.out.println("해당 유저를 못찾았어요!");
		return null;
	}

}
