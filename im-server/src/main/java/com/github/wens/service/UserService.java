package com.github.wens.service;

import com.github.wens.im.storage.DaoFactory;
import com.github.wens.im.storage.TokenDao;
import com.github.wens.im.storage.UserDao;
import com.github.wens.im.storage.domain.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wens on 16-1-12.
 */
public class UserService {

    private UserDao userDao = DaoFactory.createUserDao() ;

    private TokenDao tokenDao = DaoFactory.createTokenDao();

    public UserService(){


    }

    public String authorize(String tokenId ) {
        Token token  = tokenDao.find(tokenId) ;
        return token != null ? token.getUserId() : null ;
    }

    public List<String> findUserByGroup(String group) {
        return userDao.queryUserByGroup(group);
    }
}
