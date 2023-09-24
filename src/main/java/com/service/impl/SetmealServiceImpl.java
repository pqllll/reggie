package com.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CustomException;
import com.common.R;
import com.dto.DishDto;
import com.dto.SetmealDto;
import com.entity.Category;
import com.entity.Dish;
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

    //传过来的是套餐id，先展示原有信息
    @Override
    public SetmealDto getWithID(Long id) {

        //查询套餐基本信息，从setmeal表查询
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        //查询当前套餐对应的菜品信息，从setmeal_dish表查询
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(dishes);
        return setmealDto;


    }

    @Override
    @Transactional
    public void updateWithDishs(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        //清理当前套餐对应的菜品数据---setmeal_dish表的delete操作
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //添加当前提交过来的菜品数据---setmeal_dish表的insert操作
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes = dishes.stream().map((item)->{
            item.setId(IdWorker.getId());
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(dishes);

    }


}
