package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.CustomException;
import com.common.R;
import com.dto.DishDto;
import com.entity.Category;
import com.entity.Dish;
import com.entity.DishFlavor;
import com.entity.Setmeal;
import com.service.CategoryService;
import com.service.DishFlavorService;
import com.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("添加成功");
        dishService.saveWithDish(dishDto);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //分页构造器,DishDto没有自己的Mapper
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> DishDtopage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();

        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);

        //按序排列
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //拷贝

        BeanUtils.copyProperties(pageInfo, DishDtopage, "records");
        //进行分页
        dishService.page(pageInfo, queryWrapper);

        //拷贝
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            //浅拷贝
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //根据id查找name，在赋值
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryname = category.getName();
                dishDto.setCategoryName(categoryname);
            }
            return dishDto;
        }).collect(Collectors.toList());


        DishDtopage.setRecords(list);
        return R.success(DishDtopage);
    }

    /**
     * 根据id查找菜品
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto =dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    @PutMapping
   public  R<String> update(@RequestBody DishDto dishDto){
       dishService.updateWithFalvor(dishDto);
        return  R.success("修改成功");
    }

    /**
     * 根据菜品查找种类种的菜品
     */
    @GetMapping("/list")
    public  R<List<Dish>> list(Dish dish){

        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());

        //添加条件，状态为1（起售），才能显示出来
        queryWrapper.eq(Dish::getStatus,1);

        queryWrapper.orderByDesc(Dish::getUpdateTime).orderByAsc(Dish::getSort);

        List<Dish> list=dishService.list(queryWrapper);

        return  R.success(list);

    }


    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.in( ids!=null,Dish::getId,ids);
        List<Dish> list=dishService.list(queryWrapper);

        for (Dish dish : list) {
            Integer status = dish.getStatus();
            //如果不是在售卖,则可以删除
            if (status == 0){
                dishService.removeById(dish.getId());
            }else {
                //此时应该回滚,因为可能前面的删除了，但是后面的是正在售卖
                throw new CustomException("删除菜品中有正在售卖菜品,无法全部删除");
            }
        }
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(lambdaQueryWrapper);

        return  R.success("修改成功");

    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status,@RequestParam List<Long> ids){

        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.in(ids!=null,Dish::getId,ids);

       List<Dish> list=dishService.list(queryWrapper);

        for( Dish dish:list){

            if (dish!=null){
                dish.setStatus(status);
                dishService.updateById(dish);
            }
        }

        return R.success("修改成功");

    }

}
