package com.cos.jwtex01.repository;



import com.cos.jwtex01.model.User;

public interface UserRepository {
	public void save(User user);
	public User findByUsername(String username);
	public User login(User user);

}
