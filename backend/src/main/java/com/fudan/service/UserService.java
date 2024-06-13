package com.fudan.service;

import com.fudan.dao.UserDao;
import com.fudan.exception.RetCode;
import com.fudan.pojo.User;
import com.fudan.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    public R login(String username, String password) {
        User user = userDao.selectUserByUsername(username);
        if(user == null){
            return new R(RetCode.USERNAME_ERROR);
        }
        if(!user.getPassword().equals(password)){
            return new R(RetCode.PASSWORD_ERROR);
        }
        return new R(RetCode.USER_SUCCESS, user);
    }

    @Transactional
    public RetCode register(User user) {
        if(userDao.selectUserByUsername(user.getUserName()) != null){
            return RetCode.USERNAME_EXISTS;
        }
        userDao.insertUser(user);
        return RetCode.USER_SUCCESS;
    }

    public R getUserById(int id){
        User user = userDao.selectUserByUserId(id);
        if(user == null){
            return new R(RetCode.USER_ID_NOT_FOUND);
        }
        return new R(RetCode.USER_SUCCESS, user);
    }
}
