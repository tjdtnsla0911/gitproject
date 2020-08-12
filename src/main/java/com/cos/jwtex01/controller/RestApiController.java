package com.cos.jwtex01.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.catalina.authenticator.SpnegoAuthenticator.AuthenticateAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cos.jwtex01.config.auth.PrincipalDetails;
import com.cos.jwtex01.config.auth.SessionUser;
import com.cos.jwtex01.model.User;
import com.cos.jwtex01.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor // final 붙어있는애들 생성자 다 만들어줌
@RequestMapping("api/v1")
// @CrossOrigin //CORS 허용
public class RestApiController {

	// @InjectService = @AutoWired , 둘다 IoC하는건데
	// @AutoWired 스프링 전용, InjetService - 모든곳에서 가능한것
	// 리플렉션 - 실행타이밍에 메모리를 뒤져서 찾는것
	// AutoWired의 기본 원리는 생성자이다.
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("user")
	public String user(Authentication authentication) {
		System.out.println("user");
		PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

		System.out.println("principal : " + principal.getUser().getId());
		System.out.println("principal : " + principal.getUser().getUsername());
		System.out.println("principal : " + principal.getUser().getPassword());
		return "<h1>user</h1>";
	}

	// 모든 사람이 접근 가능
	@GetMapping("home")
	public String home() {
		return "<h1>home</h1>";
	}

	// 매니저가 접근 가능
	@GetMapping("manager/reports")
	public String reports() { //// 원래는 파라미터영역에 @AuthenticationPrincipal 하면대는데 이러면 session영역에 접근가능하다고함 , 근데이거쓰면
								//// 토큰방식에 어긋나서? 하면안댄다함 글고 안만들어진다함 타이밍문제
		return "<h1>reports</h1>";
	}

//	// 어드민이 접근 가능
//	@GetMapping("admin/user")
//	public List<User> users() {
//		return userRepository.findAll();
//	}

	@PostMapping("join")
	public String join(@RequestBody User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setRole("ROLE_USER");
		userRepository.save(user);
		return "회원가입완료";
	}
}
