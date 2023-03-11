package com.learning.lili.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.lili.entity.ShoppingCart;
import com.learning.lili.mapper.ShoppingCartMapper;
import com.learning.lili.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
