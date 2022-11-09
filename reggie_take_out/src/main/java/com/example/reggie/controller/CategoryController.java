package com.example.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.CustomException;
import com.example.reggie.common.Result;
import com.example.reggie.entity.Category;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishService;
import com.example.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;

    @PostMapping
    public Result<String> save(@RequestBody Category category) {
        log.info("add category:" + category);
        if(categoryService.save(category)) {
            return Result.success("新增菜品分类成功");
        }
        return Result.error("新增菜品分类失败");
    }

    @GetMapping("/page")
    public Result<Page<Category>> getPage(int page, int pageSize) {
        Page<Category> categoryPage = categoryService.selectPage(page, pageSize);
        if(categoryPage != null) {
            return Result.success(categoryPage);
        }
        return Result.error("系统错误");
    }

    /**
     * 删除分类，但是先要查看是否存在属于该分类的菜品和套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long ids) {
        //根据Id查询
        Integer num1 = dishService.selectNumByCategoryId(ids);
        Integer num2 = setMealService.selectNumByCategoryId(ids);

        //如果已经关联，抛出一个自定义异常
        if(num1 > 0) {
            //抛出自定义异常
            throw new CustomException("该分类下有菜品关联，不能删除");
        }
        if(num2 > 0) {
            //抛出自定义异常
            throw new CustomException("该分类下有套餐关联，不能删除");
        }
        log.info("删除分类: id=" + ids);
        if(categoryService.delete(ids)) {
            return Result.success("分类信息删除成功");
        }
        return Result.error("分类信息删除失败");
    }

    @PutMapping
    public Result<String> update(@RequestBody Category category) {
        log.info("修改分类信息:" + category.toString());
        if(categoryService.update(category)) {
            return Result.success("修改分类信息成功");
        }
        return Result.error("修改分类信息失败");
    }

    /**
     * 查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type) {
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
