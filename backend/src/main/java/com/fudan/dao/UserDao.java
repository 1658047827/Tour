package com.fudan.dao;
import com.fudan.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDao {
    @Select("select * from t_user where user_id = #{id}")
    User selectUserByUserId(int id);

    @Select("select * from t_user where user_name = #{username}")
    User selectUserByUsername(String username);

    @Insert("INSERT INTO t_user (`user_name`, `password`, `email`, `model`, `color`) VALUES (#{userName}, #{password}, #{email}, #{model}, #{color})")
    int insertUser(User user);
}
