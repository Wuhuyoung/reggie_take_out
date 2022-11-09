package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import com.example.reggie.mapper.SetmealDishMapper;
import com.example.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SetmealDishServiceImpl implements SetmealDishService {
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public boolean saveList(List<SetmealDish> setmealDishes) {
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDishMapper.insert(setmealDish);
        }
        return true;
    }

}
