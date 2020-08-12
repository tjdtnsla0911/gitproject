package com.cos.jwtex01.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.jwtex01.model.User;
import com.cos.jwtex01.repository.UserRepository;



@Service // Ioc 이제 스프링한테 맡기겟다 이런느낌?
public class UserService {
	@Autowired
	UserRepository userRepository;

	@Transactional
	public int 회원가입(User user) {
		System.out.println("회원가입 서비스");
		try {

			userRepository.save(user);
			return 1;
		} catch (Exception e) {
			e.getMessage();
		}
		return -1;
	}

	@Transactional(readOnly = true)
	public User 로그인(User user) {
		System.out.println("로그인 서비스");
		System.out.println("여기는 로그인 = "+userRepository.login(user));
		return userRepository.login(user);
	}

}