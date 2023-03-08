package com.learning.lili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.lili.common.CustomException;
import com.learning.lili.dto.DishDto;
import com.learning.lili.entity.Dish;
import com.learning.lili.entity.DishFlavor;
import com.learning.lili.entity.SetmealDish;
import com.learning.lili.mapper.DishMapper;
import com.learning.lili.service.DishFlavorService;
import com.learning.lili.service.DishService;
import com.learning.lili.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Transactional // 事务控制，同时保存菜品和口味数据
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品基本信息到菜品表dish
        this.save(dishDto);
        // 获取菜品id
        Long dishId = dishDto.getId();

        // 保存菜品口味数据到菜品口味表dish_flavor
        // 处理菜品口味数据，填充菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);
        // 对象拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        // 查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 修改菜品信息，同时更新dish表和dish_flavor表
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        // 更新菜品信息dish表
        this.updateById(dishDto);
        Long dishId = dishDto.getId();
        // 更新口味信息dish_flavor表
        // 先清理当前菜品的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(queryWrapper);
        // 再添加当前菜品的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品信息，同时删除菜品和口味
     * @param ids
     */
    @Override
    public void removeWithFlavor(List<Long> ids) {
        // 查询菜品状态，正在售卖的不能删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("菜品正在售卖中，不能删除");
        }
        // 查询菜品是否有关联套餐，有关联套餐不能删除
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getDishId, ids);
        int count1 = setmealDishService.count(queryWrapper1);
        if (count1 > 0) {
            throw new CustomException("菜品存在相关的套餐，不能删除");
        }
        // 可以删除
        // 先删除菜品信息
        this.removeByIds(ids);
        // 再删除口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(queryWrapper2);
    }
}
