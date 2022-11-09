package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.entity.Employee;

public interface EmployeeService {
    Employee selectByUsername(String username);

    boolean save(Employee employee);

    Page<Employee> selectPageByCondition(int currentPage, int pageSize, String name);

    boolean update(Employee employee);

    Employee selectById(Long id);
}
