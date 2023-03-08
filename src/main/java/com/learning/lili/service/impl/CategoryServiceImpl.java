package com.learning.lili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.lili.common.CustomException;
import com.learning.lili.entity.Category;
import com.learning.lili.entity.Dish;
import com.learning.lili.entity.Setmeal;
import com.learning.lili.mapper.CategoryMapper;
import com.learning.lili.service.CategoryService;
import com.learning.lili.service.DishService;
import com.learning.lili.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;

    /**
     * 根据id删除分类，删除前先进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        // 判断当前分类是否关联了菜品，如果关联了抛出业务异常
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据分类id进行查询
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(dishQueryWrapper);
        // 已经关联菜品，抛出业务异常
        if (count > 0) {
            throw new CustomException("当前分类已经关联了菜品，不能删除");
        }

        // 判断当前分类是否关联了套餐，如果关联了抛出业务异常
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据分类id进行查询
        setmealQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealQueryWrapper);
        // 已经关联套餐，抛出业务异常
        if (count2 > 0) {
            throw new CustomException("当前分类已经关联了套餐，不能删除");
        }

        // 正常删除分类
        super.removeById(id);
    }
}
