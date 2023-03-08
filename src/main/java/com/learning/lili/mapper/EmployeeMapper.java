package com.learning.lili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.lili.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
