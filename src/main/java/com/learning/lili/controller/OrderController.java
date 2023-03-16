package com.learning.lili.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.lili.common.BaseContext;
import com.learning.lili.common.R;
import com.learning.lili.dto.OrderDto;
import com.learning.lili.entity.OrderDetail;
import com.learning.lili.entity.Orders;
import com.learning.lili.service.OrderDetailService;
import com.learning.lili.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order) {
        log.info("Order: {}", order);
        orderService.saveWithOrderDetail(order);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize) {
        log.info("page: {}, pageSize: {}", page, pageSize);
        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        // 设置过滤条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        // 设置排序条件
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        // 进行查询
        orderService.page(pageInfo, queryWrapper);
        List<Orders> orders = pageInfo.getRecords();
        // 填充订单明细数据
        List<OrderDto> orderDtos = orders.stream().map(item -> {
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);
            LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(OrderDetail::getOrderId, orderDto.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper1);
            orderDto.setOrderDetails(orderDetails);
            orderDto.setSumNum(orderDetails.size());
            return orderDto;
        }).collect(Collectors.toList());
        pageInfo.setRecords(orderDtos);
        return R.success(pageInfo);
    }
}
