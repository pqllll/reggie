package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.OrderDetail;
import com.entity.Orders;

import java.util.List;

public interface OrdersService extends IService<Orders> {
    public void submit(Orders orders);

    public List<OrderDetail> getorderDetailByOrderID(Long orderid);
}
