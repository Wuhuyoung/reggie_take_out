package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;

import java.util.List;

public interface SetmealDishService {
    boolean saveList(List<SetmealDish> setmealDishes);


}
