package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.entity.Category;

import java.util.List;

public interface CategoryService {

    boolean save(Category category);

    Page<Category> selectPage(int page, int pageSize);

    Category selectById(Long id);

    boolean delete(Long id);

    boolean update(Category category);

    List<Category> list(Integer type);
}
