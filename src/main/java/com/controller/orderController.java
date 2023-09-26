package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.common.R;

import com.entity.*;

import com.service.OrderDetailService;
import com.service.OrdersService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/order")
public class orderController {

    @Autowired
   private OrdersService ordersService;

    @Autowired
    OrderDetailService orderDetailService;

    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize) {
        return null;

    }

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);

        return R.success("提交成功");
    }
}
