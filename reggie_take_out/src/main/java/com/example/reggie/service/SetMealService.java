package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Setmeal;

import java.util.List;

public interface SetMealService {
    Integer selectNumByCategoryId(Long id);

    boolean saveSetmeal(Setmeal setmeal);

    Page<SetmealDto> page(int page, int pageSize, String name);

    void deleteWithDish(List<Long>ids);

    List<Setmeal> list(Setmeal setmeal);
}
