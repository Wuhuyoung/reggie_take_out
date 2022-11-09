package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.entity.Category;
import com.example.reggie.mapper.CategoryMapper;
import com.example.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public boolean save(Category category) {
        int i = categoryMapper.insert(category);
        if(i > 0) return true;
        return false;
    }

    @Override
    public Page<Category> selectPage(int currentPage, int pageSize) {
        Page page = new Page<Category>(currentPage, pageSize);
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Category::getSort);
        categoryMapper.selectPage(page, lqw);
        return page;
    }

    @Override
    public Category selectById(Long id) {
        Category category = categoryMapper.selectById(id);
        return category;
    }

    @Override
    public boolean delete(Long id) {
        int i = categoryMapper.deleteById(id);
        if(i > 0) return true;
        return false;
    }

    @Override
    public boolean update(Category category) {
        int i = categoryMapper.updateById(category);
        if(i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Category> list(Integer type) {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(type != null, Category::getType, type);
        lqw.orderByAsc(Category::getSort);
        lqw.orderByDesc(Category::getUpdateTime);
        List<Category> categories = categoryMapper.selectList(lqw);
        return categories;
    }
}
