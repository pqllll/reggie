package com.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/order")
public class orderController {

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){



        return null;
    }
}
