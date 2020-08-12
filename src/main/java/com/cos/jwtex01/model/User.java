package com.cos.jwtex01.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class User {

    private long id;
    private String username;
    private String password;
    private String role;


    public List<String> getRoleList(){
    	System.out.println("model.User의 getRoleList에왔습니다 ");
        if(this.role.length() > 0){
        	System.out.println("model.User의 getRoleList의 if문 this.roles = "+this.role);
            return Arrays.asList(this.role.split(","));
        }
        return new ArrayList<>();
    }
}
