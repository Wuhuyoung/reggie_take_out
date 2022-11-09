package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.mapper.DishFlavorMapper;
import com.example.reggie.service.DishFlavorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishFlavorServiceImpl implements DishFlavorService {
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    @Transactional
    public boolean saveList(List<DishFlavor> list) {
        for (DishFlavor dishFlavor : list) {
            dishFlavorMapper.insert(dishFlavor);
        }
        return true;
    }

    @Override
    public List<DishFlavor> selectByDishId(Long id) {
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.like(id != null, DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(lqw);
        return dishFlavors;
    }

    @Override
    public void remove(Long dishId) {
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, dishId);
        dishFlavorMapper.delete(lqw);
    }
}
