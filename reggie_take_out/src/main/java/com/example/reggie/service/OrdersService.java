package com.example.reggie.service;

import com.example.reggie.entity.Orders;

public interface OrdersService {

    /**
     * 用户下单
     * @param orders
     * @return
     */
    boolean submit(Orders orders);
}
