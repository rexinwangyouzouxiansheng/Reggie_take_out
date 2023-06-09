package com.zjj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjj.reggie.common.MailUtils;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.pojo.User;
import com.zjj.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @create: 2023-04-06 16:41
 * @author: Junj_Zou
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session) throws MessagingException {
        String phone = user.getPhone();
        if (!phone.isEmpty()) {
            //随机生成一个验证码
            String code = MailUtils.achieveCode();
            log.info(code);
            //这里的phone其实就是邮箱，code是我们生成的验证码
            MailUtils.sendTestMail(phone, code);
            //验证码存session，方便后面拿出来比对
            session.setAttribute(phone, code);
            return Result.success("验证码发送成功");
        }
        return Result.error("验证码发送失败");
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session) {
        // 获取邮箱
        String phone = map.get("phone").toString();
        // 获取验证码
        String code = map.get("code").toString();

        // 从Session中获取验证码GroundTruth
        String codeInSession = session.getAttribute(phone).toString();

        //比较这用户输入的验证码和session中存的验证码是否一致
        if (code != null && code.equals(codeInSession)) {
            //如果输入正确，判断一下当前用户是否存在
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            //判断依据是从数据库中查询是否有其邮箱
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            //如果不存在，则创建一个，存入数据库
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                userService.save(user);
                user.setName("用户" + codeInSession);
            }
            //存个session，表示登录状态
            session.setAttribute("user",user.getId());
            //并将其作为结果返回
            return Result.success(user);
        }
        return Result.error("登录失败");
    }

}
