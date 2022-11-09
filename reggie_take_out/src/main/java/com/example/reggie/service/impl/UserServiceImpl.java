package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.entity.User;
import com.example.reggie.mapper.UserMapper;
import com.example.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;


    @Override
    public User selectByPhone(String phone) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPhone, phone);
        User user = userMapper.selectOne(lqw);
        return user;
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public boolean save(User user) {
        int insert = userMapper.insert(user);
        return insert > 0;
    }

    @Override
    public User selectById(Long id) {
        User user = userMapper.selectById(id);
        return user;
    }
}
