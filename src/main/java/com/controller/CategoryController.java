package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.entity.Category;
import com.entity.Employee;
import com.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {

        Page<Category> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();

        List<Category> list = categoryService.list(queryWrapper);
       
        //按序排列
        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 删除分页信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        //categoryService.removeById(ids);
        categoryService.remove(ids);
        return R.success("删除信息");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("更新成功");

    }

    @GetMapping("list")
    public R<List<Category>> list(Category category) {
        //构建构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //添加条件
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        //顺序
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        //放入
        List<Category> list = categoryService.list(lambdaQueryWrapper);

        return R.success(list);

    }
}
