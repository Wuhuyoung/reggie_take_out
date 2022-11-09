package com.example.reggie.controller;

import com.example.reggie.common.BaseContext;
import com.example.reggie.common.Result;
import com.example.reggie.entity.ShoppingCart;
import com.example.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加菜品或套餐到购物车
     * @return
     */
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {

        //设置userId
        Long userId = BaseContext.getId();
        shoppingCart.setUserId(userId);

        //根据userId和dishId/setmealId查询是否已经存在
        ShoppingCart cart = shoppingCartService.selectByUserId(shoppingCart);

        if(cart != null) {
            //如果存在，则将其数量+1
            Integer number = cart.getNumber();
            cart.setNumber(number + 1);
            shoppingCartService.update(cart);
        } else {
            //如果不存在，则添加到购物车，默认数量为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }

        return Result.success(cart);
    }

    /**
     * 减少购物车中的菜品或套餐
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public Result<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        //通过userId和dishId/setmealId查找购物车中数量
        shoppingCart.setUserId(BaseContext.getId());
        ShoppingCart cart = shoppingCartService.selectByUserId(shoppingCart);
        Integer number = cart.getNumber();

        //如果数量为1，则从购物车中删除
        if(number == 1) {
            shoppingCartService.deleteOne(cart);
            cart.setNumber(0);
        } else if(number > 1) {
            //如果数量大于1，则更新该购物车菜品的数量为number - 1
            cart.setNumber(number - 1);
            shoppingCartService.update(cart);
        }

        return Result.success(cart);
    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        Long id = BaseContext.getId();
        List<ShoppingCart> list = shoppingCartService.list(id);
        return Result.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public Result<String> delete() {
        Long id = BaseContext.getId();
        shoppingCartService.delete(id);
        return Result.success("清空购物车成功");
    }
}
