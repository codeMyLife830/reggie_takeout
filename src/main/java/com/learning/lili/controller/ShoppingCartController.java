package com.learning.lili.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.lili.common.BaseContext;
import com.learning.lili.common.R;
import com.learning.lili.entity.ShoppingCart;
import com.learning.lili.service.ShoppingCartService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
@Api(tags="购物车管理")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> save(@RequestBody ShoppingCart shoppingCart) {
        log.info("ShoppingCart：{}", shoppingCart);
        Long id = BaseContext.getCurrentId();
        log.warn("Id：{}", id);
        shoppingCart.setUserId(id);
        // 传入的数据缺少数量number
        // 1. 先查询当前用户购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, id);
        // 判断当前加入购物车的是菜品还是套餐
        if (shoppingCart.getDishId() != null) {
            // 是菜品
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            // 是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
        if (shoppingCart1 == null) {
            // 购物车不存在当前加入商品，数量设为1
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCart1 = shoppingCart;
        } else {
            shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
            shoppingCartService.updateById(shoppingCart1);
        }
        return R.success(shoppingCart1);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody Map shoppingCart) {
        Object dishId = shoppingCart.get("dishId");
        Object setmealId = shoppingCart.get("setmealId");
        log.info("DishId: {}, setmealId: {}", dishId, setmealId);
        Long id = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, id);
        if (dishId != null) {
            // 是菜品
            Long dish_id = Long.valueOf(dishId.toString());
            queryWrapper.eq(ShoppingCart::getDishId, dish_id);
        } else {
            // 是套餐
            Long setmeal_id = Long.valueOf(setmealId.toString());
            queryWrapper.eq(ShoppingCart::getSetmealId, setmeal_id);
        }
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
        shoppingCart1.setNumber(shoppingCart1.getNumber() - 1);
        if (shoppingCart1.getNumber() == 0) {
            shoppingCartService.removeById(shoppingCart1);
        } else {
            shoppingCartService.updateById(shoppingCart1);
        }
        return R.success(shoppingCart1);
    }

    @DeleteMapping("/clean")
    public R<String> clean() {
        Long id = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, id);
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        Long id = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, id);
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        return R.success(shoppingCartList);
    }

}
