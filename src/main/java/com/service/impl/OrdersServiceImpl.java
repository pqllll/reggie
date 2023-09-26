package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.BaseContext;
import com.common.CustomException;
import com.dto.OrderDto;
import com.entity.*;
import com.mapper.OrdersMapper;
import com.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    OrdersService ordersService;

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    UserService userService;

    @Autowired
    OrderDetailService orderDetailService;


    @Transactional
    public void submit(Orders orders) {
        //获取当前用户id，id查找shopcart
        Long userid = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ShoppingCart::getUserId, userid);
        List<ShoppingCart> shoppingCartList=shoppingCartService.list(queryWrapper);
        if (shoppingCartList==null||shoppingCartList.size()==0){
            throw new CustomException("购物车为空，不能支付");
        }


        //查找地址信息,用户信息
        User user=userService.getById(userid);
        Long addressBookID = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookID);
        //设置order
        long orderid=IdWorker.getId();
        orders.setId(orderid);
        orders.setNumber(String.valueOf(orderid));
        orders.setStatus(2);
        orders.setUserId(userid);
        //addressbookid+paymethod
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName()==null?"":addressBook.getProvinceName())+
                (addressBook.getCityName()==null?"":addressBook.getCityName())+
                (addressBook.getDistrictName()==null?"":addressBook.getDistrictName())+
                (addressBook.getDetail()==null?"":addressBook.getDetail()));
        orders.setConsignee(addressBook.getConsignee());
        //保证多线程不会出错
        AtomicInteger amount =new AtomicInteger(0);
        List<OrderDetail> orderDetails=  shoppingCartList.stream().map((item)->{

            OrderDetail orderDetail=new OrderDetail();

            orderDetail.setOrderId(orderid);
            // 将orderid放入中



            long orderDetailID=IdWorker.getId();
            orderDetail.setId(orderDetailID);
            orderDetail.setAmount(item.getAmount());
            BeanUtils.copyProperties(orders,orderDetail,new String[] { "amount","id" });

            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        orders.setAmount(new BigDecimal(amount.get()));

        this.save(orders);
        //向订单明细表插入多条数据
        log.info(String.valueOf(orderid));
        orderDetailService.saveBatch(orderDetails);

        //清空购物车
        shoppingCartService.remove(queryWrapper);

    }

    @Override
    public List<OrderDetail> getorderDetailByOrderID(Long orderid) {
        LambdaQueryWrapper<OrderDetail> queryWrapper=new LambdaQueryWrapper<>();
        
        return null;
    }
}
