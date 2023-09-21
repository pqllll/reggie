package com.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.DishDto;
import com.entity.Dish;

public interface DishService extends IService<Dish> {
    public void saveWithDish(DishDto dishDto);

    /**
     * 修改
     *更新dish和dishfalvor
     * @param id
     * @return
     */

    /**
     * 更新dish
     * @param id
     * @return
     */
    //根据id获取flavor
    public  DishDto getByIdWithFlavor (Long id);

    //更新口味表（口味表是另一个表）
    public void updateWithFalvor(DishDto dishDto);



}
