package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dto.DishDto;
import com.entity.Dish;
import com.entity.DishFlavor;
import com.mapper.DishMapeer;
import com.service.DishFlavorService;
import com.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapeer, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithDish(DishDto dishDto) {
        //保存菜品的信息->dish
        this.save(dishDto);

        //dishflavor 缺少 dishid -->获取id-->形成有id的实体
        Long dishid=dishDto.getId();
        List<DishFlavor> flavors=dishDto.getFlavors();
//        flavors=flavors.stream().map((item)->{
//            item.setDishId(dishid);
//            return item;
//        }).collect(Collectors.toList());
        flavors.forEach(f->f.setDishId(dishid));
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish=this.getById(id);
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询菜品口味信息
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        //排序
        lambdaQueryWrapper.orderByDesc(DishFlavor::getUpdateTime);
        List<DishFlavor> flavors=dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFalvor(DishDto dishDto) {
        this.updateById(dishDto);
        //删除当前口味
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加口味
        List<DishFlavor> flavors=dishDto.getFlavors();
                flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);


    }


}
