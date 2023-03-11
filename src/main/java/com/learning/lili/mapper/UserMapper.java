package com.learning.lili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.lili.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
