package com.example.reggie.service;


import com.example.reggie.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    boolean saveList(List<OrderDetail> orderDetailList);
}
