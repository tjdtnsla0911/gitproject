package com.cos.jwtex01.config.jwt;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwtex01.config.auth.PrincipalDetails;
import com.cos.jwtex01.model.User;
import com.cos.jwtex01.repository.UserRepository;


//인가
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	// BasicAuthenticationFilter Header전부 분석함
	// 그러고 SecurityContextHolder에 result를 넣어줌
	private UserRepository userRepository;

	// private AuthenticationManager authenticationManager; protected로 getter가 있어서
	// 할필요없음

	public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {

		super(authenticationManager);
		System.out.println("config.jwt.JwtAuthorizationFilter.java의 JwtAuthorizationFilter의 왔습니다");
		// this.authenticationManager = authenticationManager;
	}

	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {

		super(authenticationManager);
		System.out.println("config.jwt.JwtAuthorizationFilter.java의 JwtAuthorizationFilter의 왔습니다(여긴오버로딩) 그리고 userRepository = "+ userRepository);
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("config.jwt.JwtAuthorizationFilter.java의 doFilterInternal의 왔습니다.");
		// 서명하는곳
		// 서명할때 첫번째로 HEADER값 확인
		String header = request.getHeader(JwtProperties.HEADER_STRING);
		System.out.println("config.jwt.JwtAuthorizationFilter.java의 doFilterInternal의 header = "+header);
		if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
			// 빈값 확인, TOKEN의 Bearer 확인
			System.out.println("config.jwt.JwtAuthorizationFilter.java의 doFilterInternal의 if문(header에 왔습니다)");
			chain.doFilter(request, response); // 아무것도 안하고 돌려보냄
			return;
		}
		String token = request.getHeader(JwtProperties.HEADER_STRING)

				// JWT에 들어가면 안되는 값들 (공백, == , =) =은 패딩값
				.replace(JwtProperties.TOKEN_PREFIX, "") // Bearer 날려야 진정한 토큰이라서
				.replace("=", "").replace(" ", "");
		System.out.println("config.jwt.JwtAuthorizationFilter.java의 doFilterInternal의 token = "+token);
		// 5. 토큰 검증 (이게 인증이기 때문에 AuthenticationManager도 필요 없음)
		//내가 SecurityContext에 직접 접근해서 세션을 만들때 자동으로 UserDetailsService에 있는 loadByusername이 호출된다.
		String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)// 서명
				.getClaim("username").asString();
		//★json 웹 토큰 base64 암호화되서 날아와서 Secret 에서 적은 펭귄악어 이걸확인해서 verify(token)으로 서명한다.
		System.out.println("#######################################################");
		System.out.println("config.jwt.JwtAuthorizationFilter.java의 doFilterInternal의 username = "+username);

		if(username != null) {
			User user = userRepository.findByUsername(username);//여기서 유저 가져온다
			System.out.println("config.jwt.JwtAuthorizationFilter.java의 doFilterInternal의 if문의  user = "+user);

			// 인증은 토큰 검증시 끝. 인증을 하기 위해서가 아닌 스프링 시큐리티가 수행해주는 권한 처리를 위해
			// 아래와 같이 토큰을 만들어서 Authentication 객체를 강제로 만들고 그걸 세션에 저장!
			PrincipalDetails principalDetails = new PrincipalDetails(user);
			System.out.println("config.jwt.JwtAuthorizationFilter.java의 doFilterInternal의 if문의  principalDetails = "+principalDetails);
			Authentication authentication =
					new UsernamePasswordAuthenticationToken(
							principalDetails, //나중에 컨트롤러에서 DI해서 쓸 때 사용하기 편함.
							null, // 패스워드는 모르니까 null 처리, 어차피 지금 인증하는게 아니니까!!
							principalDetails.getAuthorities());
			//↑여긴 맘대로넣어도된다함 인증탈께아니니까.. 라고하는데 무슨말인지 모르겟다.
			System.out.println("config.jwt.JwtAuthorizationFilter.java의 doFilterInternal의 if문의  authentication = "+authentication);
			// 강제로 시큐리티의 세션에 접근하여 값 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);//일케하면 검증안된 토큰이만들어진다함


//		HttpSession session = request.getSession()();
//		session.setAttribute("sessionUser", user);
//
		}

		chain.doFilter(request, response);
	}
}
