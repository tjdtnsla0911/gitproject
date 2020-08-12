package com.cos.jwtex01;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class Test1 {
	
	@Test
	public void 컬렉션_테스트() {
		String[] str = {"ROLE_USER","ROLE_ADMIN","ROLE_MANAGER"};
		//배열을 넣으면 된다.
		List<String> list = Arrays.asList(str);
		for (String s : list) {
			System.out.println(s);
		}
	}
}
