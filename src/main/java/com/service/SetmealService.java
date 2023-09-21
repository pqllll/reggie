package com.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.common.R;
import com.dto.SetmealDto;
import com.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDto (SetmealDto setmealDto);

    public  void removeWithDish (List<Long> ids);
}
