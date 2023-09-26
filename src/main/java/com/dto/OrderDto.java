package com.dto;

import com.entity.OrderDetail;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends OrderDetail {
   List <OrderDetail> orderDetail;
    Long userid;
}
