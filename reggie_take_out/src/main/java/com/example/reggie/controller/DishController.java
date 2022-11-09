package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.Result;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Dish;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        log.info("dishDto=" + dishDto);
        if(dishService.saveWithFlavor(dishDto)) {
            return Result.success("新增菜品成功");
        }
        return Result.error("新增菜品失败");
    }

    /**
     * 根据id查询菜品和口味信息
     * @param id
     * @return dishDto
     */
    @GetMapping("/{id}")
    public Result<DishDto> selectByIdWithFlavor(@PathVariable Long id) {
        DishDto dishDto = dishService.selectByIdWithFlavor(id);
        return Result.success(dishDto);
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<DishDto>> selectPage(Integer page, Integer pageSize, String name) {
        Page<DishDto> dishPage = dishService.selectPage(page, pageSize, name);
        return Result.success(dishPage);
    }

    /**
     * 修改菜品
     * @param dishDto
     */
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) {
        dishService.update(dishDto);
        return Result.success("修改菜品成功");
    }

    /**
     * 查询菜品列表
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishDto>> selectLish(Dish dish) {
        List<DishDto> dishDtoList = dishService.selectList(dish);
        if(dishDtoList == null) {
            return Result.error("未查询到菜品信息");

        }
        return Result.success(dishDtoList);
    }
}
