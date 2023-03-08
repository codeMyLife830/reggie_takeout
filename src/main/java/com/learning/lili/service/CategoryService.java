package com.learning.lili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.lili.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
