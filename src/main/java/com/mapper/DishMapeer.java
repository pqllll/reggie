package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapeer extends BaseMapper<Dish> {
}
