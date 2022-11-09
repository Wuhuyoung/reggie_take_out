package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.entity.ShoppingCart;
import com.example.reggie.mapper.ShoppingCartMapper;
import com.example.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * 根据userId 和 dishId或setmealId查询
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart selectByUserId(ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        //根据userId查询
        Long userId = shoppingCart.getUserId();
        lqw.eq(ShoppingCart::getUserId, userId);

        Long dishId = shoppingCart.getDishId();
        //判断是菜品还是套餐
        if(dishId != null) { //菜品
            lqw.eq(ShoppingCart::getDishId, dishId);
        } else { //套餐
            Long setmealId = shoppingCart.getSetmealId();
            lqw.eq(ShoppingCart::getSetmealId, setmealId);
        }

        ShoppingCart cart = shoppingCartMapper.selectOne(lqw);

        return cart;
    }


    /**
     * 更新购物车
     * @param shoppingCart
     * @return
     */
    @Override
    public boolean update(ShoppingCart shoppingCart) {
        int i = shoppingCartMapper.updateById(shoppingCart);
        return i > 0;
    }

    /**
     * 添加到购物车
     * @param shoppingCart
     * @return
     */
    @Override
    public boolean save(ShoppingCart shoppingCart) {
        int insert = shoppingCartMapper.insert(shoppingCart);
        return insert > 0;
    }

    /**
     * 根据 userId查询购物车
     * @param userId
     * @return
     */
    @Override
    public List<ShoppingCart> list(Long userId) {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);
        lqw.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(lqw);
        return shoppingCarts;
    }

    /**
     * 清空购物车
     * @param userId
     * @return
     */
    @Override
    public boolean delete(Long userId) {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);
        shoppingCartMapper.delete(lqw);
        return true;
    }

    /**
     * 从购物车中删除一条菜品或套餐记录
     * @return
     */
    @Override
    public boolean deleteOne(ShoppingCart shoppingCart) {
        //根据userId和dishId/setmealId删除
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        lqw.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
        lqw.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        int delete = shoppingCartMapper.delete(lqw);
        return delete > 0;
    }
}
