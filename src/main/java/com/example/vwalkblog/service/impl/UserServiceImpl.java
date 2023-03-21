package com.example.vwalkblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vwalkblog.common.BaseContextThreadLocal;
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

    @Override
    public boolean updateUser(Users user) {
        LambdaUpdateWrapper<Users> luw = new LambdaUpdateWrapper<>();
        luw.eq(Users::getId,BaseContextThreadLocal.getCurrentId());
        if (user.getName() != null || !"".equals(user.getName())){
            luw.set(Users::getName,user.getName());
        }
        if (user.getAvatar() != null || !"".equals(user.getAvatar())){
            luw.set(Users::getAvatar,user.getAvatar());
        }
        if (user.getDescription() != null || !"".equals(user.getDescription())){
            luw.set(Users::getDescription,user.getDescription());
        }
        if (user.getSex() != null || !"".equals(user.getSex())){
            luw.set(Users::getSex,user.getSex());
        }
        if (user.getPhone() != null || !"".equals(user.getPhone())){
            luw.set(Users::getPhone,user.getPhone());
        }
        boolean update = this.update(luw);
        return update;
    }
}




