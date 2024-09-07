package com.james.reggie_takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.james.reggie_takeout.entity.Category;
import com.james.reggie_takeout.mapper.CategoryMapper;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
