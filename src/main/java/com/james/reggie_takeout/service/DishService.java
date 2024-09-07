package com.james.reggie_takeout.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.james.reggie_takeout.dto.DishDto;
import com.james.reggie_takeout.entity.Dish;

public interface DishService extends IService<Dish> {

    // 新增菜品，同时插入菜品对应的口味数据，需要操作两张表， dish, dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    // 根据 dish id（不同于 dishDto id） 获取 DishDto 对象
    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
