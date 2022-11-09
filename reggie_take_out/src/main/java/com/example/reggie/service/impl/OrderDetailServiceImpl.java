package com.example.reggie.service.impl;

import com.example.reggie.entity.OrderDetail;
import com.example.reggie.mapper.OrderDetailMapper;
import com.example.reggie.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;


    /**
     * 批量保存订单明细数据
     * @return
     */
    @Override
    public boolean saveList(List<OrderDetail> list) {
        list.stream().map((item) -> {
            orderDetailMapper.insert(item);
            return item;
        }).collect(Collectors.toList());
        return true;
    }
}
