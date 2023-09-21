package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CustomException;
import com.entity.Category;
import com.entity.Dish;
import com.entity.Setmeal;
import com.mapper.CategoryMapper;
import com.service.CategoryService;
import com.service.DishService;
import com.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService  {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {

        //
        LambdaQueryWrapper <Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        //关联1（商务套餐）
        int count1=dishService.count(dishLambdaQueryWrapper);
        if (count1>0){
            throw new com.common.CustomException("关联了菜品，不能删除");
        }

        LambdaQueryWrapper <Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);

        //关联2（儿童套餐）
        int count2=setmealService.count(setmealLambdaQueryWrapper);
        if (count2>0){
            throw new com.common.CustomException("关联了菜单，不能删除");
        }

        //通过id将菜品从dish中删除
        super.removeById(id);
    }


}
