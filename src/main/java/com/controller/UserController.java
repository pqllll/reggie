package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.R;
import com.entity.User;
import com.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public R<String> login(@RequestBody User user, HttpSession httpSession) {

        String phone=user.getPhone();

        log.info(phone);

        Long userID=user.getId();
        if (userID==null){
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            user.setId(Long.valueOf(user.getPhone()));
            queryWrapper.setEntity(user);
        }


        httpSession.setAttribute("phone",phone);

        return R.success("登陆成功");
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request){

        request.getSession().removeAttribute("user");

        return R.success("成功退出");
    }



}
