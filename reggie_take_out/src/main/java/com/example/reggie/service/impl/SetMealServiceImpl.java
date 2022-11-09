package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.CustomException;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import com.example.reggie.mapper.SetMealMapper;
import com.example.reggie.mapper.SetmealDishMapper;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private CategoryService categoryService;

    /**
     * 根据分类id查询套餐数量
     * @param id
     * @return
     */
    @Override
    public Integer selectNumByCategoryId(Long id) {
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Setmeal::getCategoryId, id);
        Integer num = setMealMapper.selectCount(lqw);
        return num;
    }

    @Override
    public boolean saveSetmeal(Setmeal setmeal) {
        setMealMapper.insert(setmeal);
        return true;
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<SetmealDto> page(int page, int pageSize, String name) {
        //分页查询
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //条件查询
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper();
        lqw.like(name != null, Setmeal::getName, name);
        lqw.orderByDesc(Setmeal::getUpdateTime);
        setMealMapper.selectPage(setmealPage, lqw);

        //页面信息拷贝
        BeanUtils.copyProperties(setmealPage, dtoPage, "records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> list = new ArrayList<>();

        //categoryName查询+拷贝
        for (Setmeal setmeal : records) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);

            Long categoryId = setmeal.getCategoryId();
            Category category = categoryService.selectById(categoryId);
            if(category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
                list.add(setmealDto);
            }
        }
        dtoPage.setRecords(list);

        return dtoPage;
    }

    /**
     * 删除套餐，同时删除关联表中的菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteWithDish(List<Long> ids) {
        //查询是否有处于启售状态的套餐
        //select count(*) from setmeal where id in (...) and status = 1;
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.in(ids != null, Setmeal::getId, ids);
        lqw.eq(Setmeal::getStatus, 1);
        Integer count = setMealMapper.selectCount(lqw);

        if(count > 0) {
            //如果有，抛出一个业务异常
            throw new CustomException("有套餐处于售卖中，不能删除");
        }

        //如果没有，先删除套餐表中的套餐
        setMealMapper.deleteBatchIds(ids);

        //再删除关联表中的菜品
        //delete from setmeal_dish where dish_id in (...)
        LambdaQueryWrapper<SetmealDish> lqw2 = new LambdaQueryWrapper<>();
        lqw2.in(ids != null, SetmealDish::getSetmealId, ids);
        setmealDishMapper.delete(lqw2);
    }

    /**
     * 查询套餐列表
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        //必须是起售状态才可以显示
        lqw.eq(Setmeal::getStatus, 1);
        lqw.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setMealMapper.selectList(lqw);
        return setmealList;
    }
}


