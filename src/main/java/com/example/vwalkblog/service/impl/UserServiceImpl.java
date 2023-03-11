package com.example.vwalkblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vwalkblog.pojo.Users;
import com.example.vwalkblog.service.UserService;
import com.example.vwalkblog.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 32580
* @description 针对表【user(用户信息)】的数据库操作Service实现
* @createDate 2023-03-09 17:19:11
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, Users> implements UserService{

}




