package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.mapper.DishMapper;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品数量
     * @param id
     * @return
     */
    @Override
    public Integer selectNumByCategoryId(Long id) {
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Dish::getCategoryId, id);
        Integer num = dishMapper.selectCount(lqw);
        return num;
    }

    /**
     * 新增菜品，同时保存口味
     * @param dishDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class) //一次操作了两张表，所以采用事务控制
    public boolean saveWithFlavor(DishDto dishDto) {
        //由于新增了菜品，需要清空该类菜品的缓存
        String key = "dish:" + dishDto.getCategoryId() + ":1";
        redisTemplate.delete(key);

        //dishDto继承自Dish，可以传入insert方法中
        dishMapper.insert(dishDto);

        //需要先将每个flavor的dish_id赋值为当前菜品的id
        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
        }

        //需要保存口味
        dishFlavorService.saveList(flavors);
        return true;
    }

    /**
     * 修改菜品
     * @param dishDto
     */
    @Override
    @Transactional
    public void update(DishDto dishDto) {
        //菜品修改
        dishMapper.updateById(dishDto);

        //由于新增了菜品，需要清空该类菜品的缓存
        String key = "dish:" + dishDto.getCategoryId() + ":1";
        redisTemplate.delete(key);

        //删除之前菜品对应的口味
        dishFlavorService.remove(dishDto.getId());

        //新增口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //需要先将每个flavor的dish_id赋值为当前菜品的id
        Long id = dishDto.getId();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
        }
        dishFlavorService.saveList(flavors);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @param name
     * @return
     */
    @Override
    public Page<DishDto> selectPage(Integer page, Integer size, String name) {
        //分页查询器
        Page<Dish> dishPage = new Page<>(page, size);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件查询器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null, Dish::getName, name);
        lqw.orderByDesc(Dish::getUpdateTime);
        dishMapper.selectPage(dishPage, lqw);

        //页面基本信息赋值,records不拷贝
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        //拷贝records信息，即List<Dish>->List<DishDto>
        List<Dish> records = dishPage.getRecords();
        List<DishDto> list = new ArrayList<>();
        for (Dish dish : records) {
            DishDto dishDto = new DishDto();
            //获取categoryName
            Long id = dish.getCategoryId();
            String categoryName = categoryService.selectById(id).getName();
            //给DishDto赋值
            BeanUtils.copyProperties(dish, dishDto);
            dishDto.setCategoryName(categoryName);
            list.add(dishDto);
        }

        //重新赋值records
        dishDtoPage.setRecords(list);

        return dishDtoPage;
    }

    /**
     * 根据id查询菜品和口味信息
     * @param id
     * @return dishDto
     */
    @Override
    public DishDto selectByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        //查询菜品信息
        Dish dish = dishMapper.selectById(id);
        BeanUtils.copyProperties(dish, dishDto);
        //查询口味信息
        List<DishFlavor> dishFlavors = dishFlavorService.selectByDishId(id);
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    /**
     * 查询菜品列表
     * @param dish
     * @return
     */
    @Override
    public List<DishDto> selectList(Dish dish) {
        List<DishDto> dishDtoList = null;

        //动态构造key，根据菜品分类来缓存
        String key = "dish" + ":" + dish.getCategoryId() + ":1";
        //从缓存中查找是否存在菜品
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //如果存在，直接返回
        if(dishDtoList != null) {
            return dishDtoList;
        }

        //如果不存在，查找数据库

        //构造查询条件
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper();
        lqw.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //增加查询条件，status必须为1，即是起售状态
        lqw.eq(Dish::getStatus, 1);
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //查询
        List<Dish> dishes = dishMapper.selectList(lqw);

        dishDtoList = new ArrayList<>();

        for (Dish dish1 : dishes) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1, dishDto);
            //查询菜品口味
            List<DishFlavor> dishFlavors = dishFlavorService.selectByDishId(dish1.getId());
            dishDto.setFlavors(dishFlavors);
            dishDtoList.add(dishDto);
        }

        //将数据存入缓存中，并设置过期时间
        redisTemplate.opsForValue().set(key, dishDtoList, 1, TimeUnit.HOURS);

        return dishDtoList;
    }


}

