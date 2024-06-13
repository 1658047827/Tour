package com.fudan.controller;

import com.fudan.exception.RetCode;
import com.fudan.pojo.User;
import com.fudan.service.UserService;
import com.fudan.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/login")
    public R login(String userName, String password) {
        return userService.login(userName, password);


    }

    @PostMapping("/register")
    public R register(@RequestBody User user) {
//        System.out.println(user);
        RetCode retCode = userService.register(user);
        return new R(retCode);
    }
    @GetMapping("/user/{id}")
    public R getUserById(@PathVariable("id") int id) {
        return userService.getUserById(id);
    }

}
