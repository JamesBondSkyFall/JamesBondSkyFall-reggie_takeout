package com.james.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.james.reggie_takeout.common.CustomException;
import com.james.reggie_takeout.entity.Category;
import com.james.reggie_takeout.entity.Combo;
import com.james.reggie_takeout.entity.Dish;
import com.james.reggie_takeout.mapper.CategoryMapper;
import com.james.reggie_takeout.service.CategoryService;
import com.james.reggie_takeout.service.ComboService;
import com.james.reggie_takeout.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    /**
     * 根据 id 删除分类，删除前需要判断，当前分类是否已经关联了菜品或套餐
     * 如果已经关联菜品dish，抛出业务异常
     * 如果已经关联套餐combo，抛出业务异常
     * 否则删除分类
     * @param id
     */


    @Autowired
    private DishService dishService;

    @Autowired
    private ComboService comboService;

    @Override
    public void remove(Long ids) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        //LambdaQueryWrapper<Dish> dishLambdaQueryWrapper1 = dishLambdaQueryWrapper;
        int count_dish = dishService.count(dishLambdaQueryWrapper);
        //如果已经关联菜品dish，抛出业务异常
        if(count_dish > 0){
            throw new CustomException("The current category is related to dish, and cannot be deleted");
        }

        LambdaQueryWrapper<Combo> comboLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件，根据分类id进行查询
        comboLambdaQueryWrapper.eq(Combo::getCategoryId,ids);
        //LambdaQueryWrapper<Dish> dishLambdaQueryWrapper1 = dishLambdaQueryWrapper;
        int count_combo = comboService.count(comboLambdaQueryWrapper);
        //如果已经关联套餐combo，抛出业务异常
        if(count_combo > 0){
            throw new CustomException("The current category is related to combo, and cannot be deleted");
        }

        // 正常删除分类
        super.removeById(ids);
    }
}
