package com.example.vwalkblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.vwalkblog.common.BaseContextThreadLocal;
import com.example.vwalkblog.common.ValidateCodeUtils;
import com.example.vwalkblog.dto.BlogDto;
import com.example.vwalkblog.pojo.Blog;
import com.example.vwalkblog.pojo.Users;
import com.example.vwalkblog.respR.Result;
import com.example.vwalkblog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@ResponseBody
@CrossOrigin(origins = "*",maxAge = 3600)
@Slf4j
public class userController {

    @Autowired
    private UserService us;
    // 退出登录
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return Result.success("退出成功");
    }

    @PostMapping("/sign_in_pwd")
    public Result<Users> login(HttpSession session, @RequestBody Users user){
        LambdaQueryWrapper<Users> lqw = new LambdaQueryWrapper();
        lqw.eq(Users::getPhone, user.getPhone());
        Users one = us.getOne(lqw);
        if (Objects.isNull(one)){
            return Result.error("登陆失败，此用户未注册");
        }
        String password = user.getPwd();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        lqw.eq(Users::getPwd, password);
        one = us.getOne(lqw);
        if (Objects.isNull(one)){
            return Result.error("登陆失败，用户名或密码错误");
        }
        session.setAttribute("user",one);
        return Result.success(one);
    }

    @PostMapping("/sign_up_pwd")
    public Result<String> signup(@RequestBody Users user){
        LambdaQueryWrapper<Users> lqw = new LambdaQueryWrapper();
        lqw.eq(Users::getPhone, user.getPhone());
        Users one = us.getOne(lqw);
        if (!Objects.isNull(one)){
            return Result.error("注册失败，此用户已注册");
        }
        String password = user.getPwd();
        user.setPwd(DigestUtils.md5DigestAsHex(password.getBytes()));
        boolean save = us.save(user);
        return save ? Result.success("注册成功") : Result.error("注册失败");
    }

    @GetMapping("/{userId}")
    public Result<Users> getUserById(@PathVariable Long userId){
        return Result.success(us.getById(userId));
    }

    // 修改user
    @PutMapping()
    public Result<String> update(@RequestBody Users user){
        boolean update = us.updateUser(user);
        return update ? Result.success("修改成功") : Result.error("修改失败");
    }

}
