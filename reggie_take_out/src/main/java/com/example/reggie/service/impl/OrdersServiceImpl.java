package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.CustomException;
import com.example.reggie.entity.*;
import com.example.reggie.mapper.OrdersMapper;
import com.example.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @Override
    @Transactional
    public boolean submit(Orders orders) {
        //获得当前用户id
        Long userId = BaseContext.getId();
        //查询用户信息
        User user = userService.selectById(userId);

        //查询购物车信息
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(userId);
        if(shoppingCartList == null) {
            throw new CustomException("购物车为空，不能下单");
        }

        //查询地址信息
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null) {
            throw new CustomException("用户地址有误，不能下单");
        }

        //组装订单信息
        long orderId = IdWorker.getId(); //自己用代码生成，后面订单号以及订单明细需要用到

        //组装订单明细数据，同时获取总金额（为了保证安全，金额必须自己计算，不能从前端传过来）
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map((item) -> {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(orderId);
            detail.setNumber(item.getNumber());
            detail.setDishFlavor(item.getDishFlavor());
            detail.setDishId(item.getDishId());
            detail.setSetmealId(item.getSetmealId());
            detail.setName(item.getName());
            detail.setImage(item.getImage());
            detail.setAmount(item.getAmount());

            BigDecimal number = new BigDecimal(item.getNumber());
            BigDecimal bigDecimal = item.getAmount().multiply(number);
            amount.addAndGet(bigDecimal.intValue());
            return detail;
        }).collect(Collectors.toList());


        //组装订单数据
        orders.setId(orderId); //传值后会使用自己设置的id值
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));


        //保存到订单表（一条数据）
        ordersMapper.insert(orders);

        //保存到订单明细表（多条数据）
        orderDetailService.saveList(orderDetailList);

        //删除购物车中该用户的菜品/套餐信息
        shoppingCartService.delete(userId);

        return true;
    }
}
