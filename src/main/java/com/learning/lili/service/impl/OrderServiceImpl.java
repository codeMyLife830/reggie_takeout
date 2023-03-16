package com.learning.lili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.lili.common.BaseContext;
import com.learning.lili.common.CustomException;
import com.learning.lili.entity.*;
import com.learning.lili.mapper.OrderMapper;
import com.learning.lili.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Override
    @Transactional
    public void saveWithOrderDetail(Orders order) {
        // 查询购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空，不能下单");
        }
        // 查询用户信息
        User user = userService.getById(BaseContext.getCurrentId());
        // 查询地址信息
        AddressBook addressBook = addressBookService.getById(order.getAddressBookId());
        if (addressBook == null) {
            throw new CustomException("地址信息有误，不能下单");
        }
        // 生成订单号
        Long orderId = IdWorker.getId();
        // 计算总价
        AtomicInteger amount = new AtomicInteger(0);
        // 生成订单明细数据
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map(item -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            orderDetail.setName(item.getName());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setSetmealId(item.getSetmealId());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        // 生成订单信息
        order.setId(orderId);
        order.setNumber(String.valueOf(orderId));
        order.setStatus(2);
        order.setUserId(BaseContext.getCurrentId());
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setAmount(new BigDecimal(amount.get()));
        order.setPhone(user.getPhone());
        order.setUserName(user.getName());
        order.setConsignee(addressBook.getConsignee());
        order.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        // 插入订单数据
        this.save(order);

        // 插入订单明细数据
        orderDetailService.saveBatch(orderDetailList);

        // 移除购物车数据
        shoppingCartService.remove(queryWrapper);
    }
}
