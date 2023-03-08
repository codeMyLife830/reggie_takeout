package com.learning.lili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.lili.dto.SetmealDto;
import com.learning.lili.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);

    public SetmealDto getByIdWithDish(Long id);

    public void updateWithDish(SetmealDto setmealDto);
}
