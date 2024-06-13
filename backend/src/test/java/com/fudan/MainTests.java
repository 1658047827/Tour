package com.fudan;

import com.fudan.dao.UserDao;
import com.fudan.exception.RetCode;
import com.fudan.pojo.User;
import com.fudan.service.UserService;
import com.fudan.util.R;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MainTests {
    @Autowired
    UserDao userDao;
    @Autowired
    UserService userService;
    @Test
    public void contextLoads() {
        User user = userDao.selectUserByUsername("erw");
        user.setUserName("erew");
        user.setPassword("123456");
        user.setColor("red");
        user.setModel("police");
        user.setEmail("1@qq.com");
//        RetCode register = userService.register(user);
        R ret = userService.getUserById(1);

        System.out.println(ret);
    }
}
