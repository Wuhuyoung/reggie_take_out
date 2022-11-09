package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.entity.Employee;
import com.example.reggie.mapper.EmployeeMapper;
import com.example.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper mapper;

    @Override
    public Employee selectByUsername(String username) {
        return mapper.selectByUsername(username);
    }

    @Override
    public boolean save(Employee employee) {
        int i = mapper.insert(employee);
        if(i > 0) return true;
        else return false;
    }

    @Override
    public Page<Employee> selectPageByCondition(int currentPage, int pageSize, String name) {
        //分页查询page对象
        Page<Employee> page = new Page<>(currentPage, pageSize);
        //条件查询
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null, Employee::getName, name);
        lqw.orderByDesc(Employee::getUpdateTime);
        //执行查询
        mapper.selectPage(page, lqw);
        return page;
    }

    @Override
    public boolean update(Employee employee) {
        int i = mapper.updateById(employee);
        if(i > 0) return true;
        return false;
    }

    @Override
    public Employee selectById(Long id) {
        return mapper.selectById(id);
    }
}
