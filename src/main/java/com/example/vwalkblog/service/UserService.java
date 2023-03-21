package com.example.vwalkblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.vwalkblog.pojo.Users;

/**
* @author 32580
* @description 针对表【user(用户信息)】的数据库操作Service
* @createDate 2023-03-09 15:17:39
*/
public interface UserService extends IService<Users> {

    boolean updateUser(Users user);
}
