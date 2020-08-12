package com.cos.jwtex01.config.jwt;


//다른곳에서 이것을 불러들여서 쓰면 실수할일이 적어진다.
//그리고 여기 값만 바꾸면 되기 때문에 편해짐
public interface JwtProperties {
	//public static final 생략
	
	String SECRET = "펭귄악어"; //우리서버만 알고 있는 비밀값
	int EXPIREATION_TIME=864000000; //10일 (1/1000초)
	String TOKEN_PREFIX="Bearer "; 
	String HEADER_STRING="Authorization";
}
