package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.dto.SetmealDto;
import com.entity.Category;
import com.entity.Employee;
import com.entity.Setmeal;
import com.entity.SetmealDish;
import com.service.CategoryService;
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
}
