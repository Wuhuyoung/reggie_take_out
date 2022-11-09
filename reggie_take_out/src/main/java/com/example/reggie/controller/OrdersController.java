package com.example.reggie.controller;

import com.example.reggie.common.Result;
import com.example.reggie.entity.Orders;
import com.example.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders) {
        log.info("订单数据:" + orders);
        boolean flag = ordersService.submit(orders);
        if(!flag) {
            return Result.error("下单失败");
        }
        return Result.success("订单下单成功");
    }
}
