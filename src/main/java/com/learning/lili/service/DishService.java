package com.learning.lili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.lili.dto.DishDto;
import com.learning.lili.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    /**
     * 新增菜品同时插入菜品对应的口味数据，需要操作两张表，dish、dish_flavor
     * @param dishDto
     */
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    public void removeWithFlavor(List<Long> ids);
}
