package com.learning.lili.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.lili.common.R;
import com.learning.lili.dto.DishDto;
import com.learning.lili.entity.Category;
import com.learning.lili.entity.Dish;
import com.learning.lili.entity.DishFlavor;
import com.learning.lili.service.CategoryService;
import com.learning.lili.service.DishFlavorService;
import com.learning.lili.service.DishService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
@Api(tags="菜品管理")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("新增菜品：{}", dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 创建分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        // 创建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        // 添加排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            // 对象拷贝
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value="dishCache", key="#dish.categoryId + '_' + #dish.status")
    public R<List<DishDto>> list(Dish dish) {
        // 构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        // 添加口味信息
        List<DishDto> dishDtoList = new ArrayList<>();
        // 将菜品信息拷贝到dishDtoList，并添加口味信息
        dishDtoList = list.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            // 查询菜品对应口味信息
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
            List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(flavors);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

    /**
     * 删除菜品，支持单个和批量操作
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("删除菜品：{}", ids.toString());
        dishService.removeWithFlavor(ids);
        return R.success("删除菜品成功");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Long status, @RequestParam List<Long> ids) {
        log.info("更新菜品状态：{}, {}", status.toString(), ids.toString());
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        List<Dish> dishes = dishService.list(queryWrapper);
        dishes = dishes.stream().map((item) -> {
            item.setStatus(Math.toIntExact(status));
            return item;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishes);
        return R.success("更新菜品成功");
    }
}
