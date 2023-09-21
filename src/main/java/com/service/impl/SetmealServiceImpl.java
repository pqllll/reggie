package com.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CustomException;
import com.common.R;
import com.dto.SetmealDto;
import com.entity.Category;
import com.entity.Setmeal;
import com.entity.SetmealDish;
import com.mapper.SetmealMapper;
import com.service.CategoryService;
import com.service.SetmealDishService;
import com.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    SetmealService setmealService;

    @Override
    @Transactional
    public void saveWithDto(SetmealDto setmealDto) {

        //插入setmeal
        setmealService.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.forEach(f -> f.setSetmealId(setmealDto.getId()));

        //插入setmeal-dish
        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {

        //删除；删除套餐；两个表，套餐表：setmeal，套餐dish表：setmeal-dish
        //1.删除setmeal,看套餐是否在出售1，若出售则不删除
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.in(Setmeal::getId,ids);

        queryWrapper.eq(Setmeal::getStatus,1);

        int count =this.count(queryWrapper);

        if (count>0){

            throw new CustomException("套餐正在售卖中，不能删除");

        }

        //若不在售卖则删除

        this.remove(queryWrapper);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(lambdaQueryWrapper);


    }
}
