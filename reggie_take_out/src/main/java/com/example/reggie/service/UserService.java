package com.example.reggie.service;

import com.example.reggie.entity.User;

public interface UserService {

    User selectByPhone(String phone);

    boolean save(User user);

    User selectById(Long id);
}
