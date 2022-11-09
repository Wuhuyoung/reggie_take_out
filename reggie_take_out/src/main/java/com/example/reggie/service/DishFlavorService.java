package com.example.reggie.service;

import com.example.reggie.entity.DishFlavor;

import java.util.List;

public interface DishFlavorService {
    boolean saveList(List<DishFlavor> list);

    List<DishFlavor> selectByDishId(Long id);

    void remove(Long dishId);
}
