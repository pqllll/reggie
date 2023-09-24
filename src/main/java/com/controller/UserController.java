package com.controller;

import com.common.R;
import com.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public R<String> login(@RequestBody Map map, HttpSession httpSession) {
        log.info(map.toString());
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        return R.success("登陆成功");
    }


}
