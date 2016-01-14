package com.github.wens.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wens on 16-1-12.
 */
public class UserService {

    private Map<String,String> tokens ;

    public UserService(){
        tokens = new HashMap<>() ;
        tokens.put("token123" , "123") ;
        tokens.put("token456" , "456") ;
        tokens.put("token789" , "789") ;

    }

    public String authorize(String token) {
        return tokens.get(token);
    }

    public List<String> findUserByGroup(String group) {
        return null;
    }
}
