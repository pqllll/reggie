package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.dto.DishDto;
import com.dto.SetmealDto;
import com.entity.*;
import com.service.CategoryService;
import com.service.DishService;
import com.service.SetmealDishService;
import com.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    SetmealService setmealService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    DishService dishService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {

        //在Service曾实现save
        setmealService.saveWithDto(setmealDto);

        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page(page, pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.like(name != null, Setmeal::getName, name);

        //按序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);

        //缺少categories，通过dto来得到
        Page<SetmealDto> dtoPage = new Page<>();

        //records的属性不同，所以不能通过BeanUtils直接拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");

        /**
         * 获取records
         * 建立一个list（setmealdto）
         * 通过id-》category-》name
         */


        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {

            SetmealDto setmealDto = new SetmealDto();

            //拷贝普通信息
            BeanUtils.copyProperties(item, setmealDto);

            Long cateId = item.getCategoryId();

            Category category = categoryService.getById(cateId);

            if (category != null) {

                String catename = category.getName();

                setmealDto.setCategoryName(catename);

            }

            return setmealDto;

        }).collect(Collectors.toList());

        dtoPage.setRecords(list);


        return R.success(dtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        setmealService.removeWithDish(ids);

        return R.success("删除成功");

    }
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status,@RequestParam List<Long> ids){

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.in(ids!=null,Setmeal::getId,ids);

        List<Setmeal> list=setmealService.list(queryWrapper);

        for( Setmeal setmeal:list){

            if (setmeal!=null){
                setmeal.setStatus(status);
                setmealService.updateById(setmeal);
            }
        }

        return R.success("修改成功");

    }
    /**
     * update
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getInfromation(@PathVariable Long id){

        SetmealDto  setmealDto=setmealService.getWithID(id);

        return  R.success(setmealDto);
    }
    @PutMapping
    public  R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDishs(setmealDto);
       return R.success("修改成功");
    }

    @GetMapping("/list")
    public  R<List<Setmeal>> list(Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());

        //添加条件，状态为1（起售），才能显示出来
        queryWrapper.eq(Setmeal::getStatus,1);

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list=setmealService.list(queryWrapper);

        return  R.success(list);

    }

    /**
     * 点击查看套餐中的菜品
     */
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> dish(@PathVariable("id") Long SetmealId) {
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, SetmealId);
        //获取套餐里面的所有菜品  这个就是SetmealDish表里面的数据
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        List<DishDto> dishDtos = list.stream().map((setmealDish) -> {
            DishDto dishDto = new DishDto();
            //将套餐菜品关系表中的数据拷贝到dishDto中
            BeanUtils.copyProperties(setmealDish, dishDto);
            //这里是为了把套餐中的菜品的基本信息填充到dto中，
            // 比如菜品描述，菜品图片等菜品的基本信息
            Long dishId = setmealDish.getDishId();
            Dish dish = dishService.getById(dishId);
            //将菜品信息拷贝到dishDto中
            BeanUtils.copyProperties(dish, dishDto);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtos);
    }


}
