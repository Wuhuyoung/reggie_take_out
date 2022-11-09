package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Dish;

import java.util.List;

public interface DishService {
     Integer selectNumByCategoryId(Long id);

     boolean saveWithFlavor(DishDto dishDto);

     Page<DishDto> selectPage(Integer page, Integer size, String name);

     DishDto selectByIdWithFlavor(Long id);

     List<DishDto> selectList(Dish dish);

     void update(DishDto dishDto);
}
