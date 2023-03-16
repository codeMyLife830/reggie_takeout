package com.learning.lili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.lili.entity.Orders;

public interface OrderService extends IService<Orders> {
    public void saveWithOrderDetail(Orders order);
}
