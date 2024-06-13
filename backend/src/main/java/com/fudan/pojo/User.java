package com.fudan.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    int userId;
    String userName;
    String password;
    String email;
    String model;
    String color;
}
