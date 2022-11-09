package com.example.reggie.service;

import com.example.reggie.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    ShoppingCart selectByUserId(ShoppingCart shoppingCart);

    boolean update(ShoppingCart shoppingCart);

    boolean save(ShoppingCart shoppingCart);

    List<ShoppingCart> list(Long userId);

    boolean delete(Long userId);

    boolean deleteOne(ShoppingCart shoppingCart);
}
