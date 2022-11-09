package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.Result;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import com.example.reggie.service.SetMealService;
import com.example.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新建套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @Transactional
    public Result<String> save(@RequestBody SetmealDto setmealDto) {
        //存储套餐信息
        setMealService.saveSetmeal(setmealDto);

        //存储套餐菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long id = setmealDto.getId();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(id);
        }
        setmealDishService.saveList(setmealDishes);
        return Result.success("新建套餐成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<SetmealDto> dtoPage = setMealService.page(page, pageSize, name);
        return Result.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("删除套餐" + ids);

        setMealService.deleteWithDish(ids);
        return Result.success("删除套餐成功");
    }

    /**
     * 查询套餐列表
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public Result<List<Setmeal>> list(Setmeal setmeal) {
        List<Setmeal> list = setMealService.list(setmeal);
        return Result.success(list);
    }
}
