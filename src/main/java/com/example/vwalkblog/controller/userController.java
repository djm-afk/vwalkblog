package com.example.vwalkblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    @Autowired
    private StringRedisTemplate redisTemplate;

    // 发送手机验证码
    @PostMapping("/sendMsg")
    public Result<String> senMsg(@RequestBody Users user){
        // 获取手机号
        String phone = user.getPhone();
        if (!StringUtils.isEmpty(phone)){
            // 生成随机4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info(" code={} "+code);
            // 调用阿里云提供的短信服务API发送短信
//            SMSUtils.sendMessage("DJM的博客","SMS_269430617",phone,code);
            // 将生成的验证码保存
//            session.setAttribute(phone,code);
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(phone,code,5, TimeUnit.MINUTES);
            return Result.success("短信发送成功");
        }
        return Result.error("短信发送失败");
    }

    // 手机验证码登录（移动端）
    @PostMapping("/login")
    @Transactional
    public Result<Users> login(HttpSession session, @RequestBody Map map){
        // 获取手机号
        String phone = (String) map.get("phone");
        // 获取验证码
        String code = (String) map.get("code");
        log.info(map.toString());

        String codeInRedis = redisTemplate.opsForValue().get(phone);
        log.info(codeInRedis);

        LambdaQueryWrapper<Users> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Users::getPhone,phone);
        Users userOne = us.getOne(lqw);
        if (!Objects.isNull(codeInRedis) && codeInRedis.equals(code)){
            // 判断当前手机号是否为新用户
            if (Objects.isNull(userOne)){
                // 自动注册
                userOne = new Users();
                userOne.setPhone(phone);
                boolean save = us.save(userOne);
            }
            // 登录成功删除redis缓存的验证码
            redisTemplate.delete(phone);
            session.setAttribute("user",userOne);
            return Result.success(userOne);
        }else {
            return Result.error("登录失败！");
        }

    }
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


}
