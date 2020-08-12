package com.cos.jwtex01.config.jwt;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwtex01.config.auth.PrincipalDetails;
import com.cos.jwtex01.dto.LoginRequestDto;
import com.cos.jwtex01.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	//모든것을 오버라이드해서 sysout 찍으면 순서를 알 수 있음

	//@RequiredArgsConstructor로 DI했음
	private final AuthenticationManager authenticationManager;


	//1번 Authentication 객체 만들어서 리턴 => 의존 : AuthenticationManager (필터체인으로 가서 또 다른 것을 타게된다는것)

	//인증 요청시에 실행되는 함수(attemptAuthentication) => /login 일때만

	//여기서 request랑 response를 변형도 가능
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("config.jwt.JwtAuthentcationFilter.java의 attemptAuthentication 에  진입");

		//request에서 username , password를 파싱해서 자바 Object로 받기
		//objectmapper를 쓰면 꺼내짐
		ObjectMapper om = new ObjectMapper();
		LoginRequestDto loginRequestDto = null;

		//리퀘스트 안에 있는 정보 파싱
		try {
			//InputStream으로 받은 데이터를 Dto로 바꿔줌
			//블로그에 ObjectMapper에 대한 정리
			//Username password가 저장됨
			System.out.println("config.jwt.JwtAuthentcationFilter.java 의 attemptAuthentication의  loginRequestDto하러옴 ");
			loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class); //여기서파싱함

			System.out.println("config.jwt.JwtAuthentcationFilter.java 의 attemptAuthentication의  loginRequestDto=  "+loginRequestDto);
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("config.jwt.JwtAuthentcationFilter.java 의 attemptAuthentication의  의 try후 토큰생성한다고함");

		//유저네임 패스워드 토큰 생성
		//principal = 인증 주체
		//credentials = 비밀번호
		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(
						loginRequestDto.getUsername(),
						loginRequestDto.getPassword()
						);
		System.out.println("config.jwt.JwtAuthentcationFilter.java 의 attemptAuthentication의 authenticationToken = "+authenticationToken);
		System.out.println(authenticationToken.getCredentials()); //비밀번호
		System.out.println(authenticationToken.getName()); //ssar
		System.out.println(authenticationToken.getPrincipal()); //ssar
		System.out.println(authenticationToken.getAuthorities()); //USER ROLE???
		System.out.println("JwtAuthenticationFilter : 토큰생성 완료");

		//authenticate() 함수가 호출 되면 인증 provider가 userDetailService의
		//loadUserByUsername(토큰의 첫번째 파라메터)를 호출하고
		//UserDetails를 리턴 받아서 토큰의 두번째 파라메터(credential)과
		//UserDetails(DB값)의 getPassword() 함수로 비교해서 동일하면
		// Authentication 객체를 만들어서 필터체인으로 리턴해준다.

		//TIP : 인증 프로바이더의 디폴트 서비스는 UserDetailsServie 타입
		//TIP : 인증 프로바이더의 디폴트 암호화 방식은 BCryptePassword
		//결론은 인증 프로바이더에게 알려줄 필요가 없음.
		System.out.println("config.jwt.JwtAuthentcationFilter.java 의 attemptAuthentication의 Authentication 실행 전");
		Authentication authentication =
				authenticationManager.authenticate(authenticationToken); //provider의 일을 위임하고 있음.
		System.out.println("config.jwt.JwtAuthentcationFilter.java 의 attemptAuthentication의 Authentication 실행 후 = "+authentication);
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("config.jwt.JwtAuthentcationFilter.java 의 attemptAuthentication의 principalDetails = "+principalDetails);
		System.out.println("Authentication : "+principalDetails.getUser().getUsername());
		System.out.println("config.jwt.JwtAuthentcationFilter.java 의 attemptAuthentication의 authentication (최종)= "+authentication);
		return authentication;
	}


	//JWT Token 생성해서 response에 담아주기(header)
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		System.out.println("config.jwt.JwtAuthentcationFilter.java 의 successfulAuthentication에옴 ");

		PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
		System.out.println("config.jwt.JwtAuthentcationFilter.java 의 successfulAuthentication의 principalDetails =  "+principalDetails);
		//클레임은 페이로드에 들어감 선택적이나 iss-토큰 발급자,sub-토큰제목,exp-만료기간 이정도는 넣는게 좋다
		//비공개 클레임이 제일 중요하다. 유저정보에서 민감하지 않는것들만 넣는다.(ex : 패스워드, 이메일, 번호)
		//Payload를 열때 토큰을 검증하고 만료되었는지보고
		String jwtToken = JWT.create()
				.withSubject(principalDetails.getUsername())//sub
				.withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIREATION_TIME))//만료시간 10일
				.withClaim("id", principalDetails.getUser().getId())//PK ㅣㅂ공개클레임
				.withClaim("username", principalDetails.getUser().getUsername())
				.sign(Algorithm.HMAC512(JwtProperties.SECRET.getBytes()));//getBytes하면 조금더 ?
		//여기?

		System.out.println("config.jwt.JwtAuthentcationFilter.java 의 successfulAuthentication의 jwtToken =  "+jwtToken); // 여기서 토큰을만듬
		response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken);//헤더
		//쿠키에 담을때 set-cookies 쿠키값에는 여러가지 있는데 ;으로 나뉘어져 있음
		//이건 예시라 아님 밑에 두개 response.addHeader("set-cookies", JwtProperties.TOKEN_PREFIX+jwtToken);//헤더
		//Cookie cookie = new Cookie("Authorization",jwtToken);
		//response.addCookie(cookie);
	}
}
