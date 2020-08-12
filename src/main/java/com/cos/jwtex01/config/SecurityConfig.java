package com.cos.jwtex01.config;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cos.jwtex01.config.jwt.JwtAuthenticationFilter;
import com.cos.jwtex01.config.jwt.JwtAuthorizationFilter;
import com.cos.jwtex01.config.jwt.MyFilter;
import com.cos.jwtex01.repository.UserRepository;

@Configuration
@EnableWebSecurity //시큐리티 활성화 -> 기본 스프링 필터체인에 등록
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private UserRepository userRepository;

	@Bean//@EnableWebSecurity로 인해서 IOC될때 같이 된다.
	public BCryptPasswordEncoder passwordEncoder() {
		System.out.println("config.SecurityConfig.java의 passwordEncoder에 왔습니다  ");
		return new BCryptPasswordEncoder();
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		System.out.println("config.SecurityConfig.java의 configure에 왔습니다  ");
		http //stateful 사용 안할려고 설정
				.csrf().disable()//csrf는 필요가 없음, 이게 있으면 postman 작동이 안함
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//세션 사용X
			.and()
				.formLogin().disable() //form 로긴 막음, 주소로 갈때 필터로 낚아채던지, formLogin 쓰던지
				.httpBasic().disable() //http Jsession방식 사용안함
				//필터 추가
				.addFilter(new JwtAuthenticationFilter(authenticationManager())) //내가 만든 인증 필터
				.addFilter(new JwtAuthorizationFilter(authenticationManager(),userRepository)) //내가 만든 인증 필터
				.authorizeRequests()//모든 권한 요청에 대해서
				//antMatchers를 걸고 네거티브 방식 사용
				.antMatchers("/api/v1/user/**")//일로가는건내가잡겟다
				  .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")//ㅇ임마들만허용ㅇ한다
				.antMatchers("/api/v1/manager/**")
					.access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
				.antMatchers("/api/v1/admin/**")
					.access("hasRole('ROLE_ADMIN')")
				.anyRequest().permitAll();//나머지는 다허용
				//.access denied page로 검색
		System.out.println("config.SecurityConfig.java의 configure에 끝나고 http =  "+http);

				//.authenticated()을 걸면 위 페이지 외에 모두 인증이 필요함
			//.and()
			//이렇게하면 모든곳에서 필터가 실행된다.
			//.addFilterBefore(new MyFilter(), UsernamePasswordAuthenticationFilter.class);
	}
}
