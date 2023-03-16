package com.learning.lili.dto;

import com.learning.lili.entity.OrderDetail;
import com.learning.lili.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;

    private Integer sumNum;
}
