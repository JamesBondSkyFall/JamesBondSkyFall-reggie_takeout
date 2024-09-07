package com.james.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.james.reggie_takeout.dto.DishDto;
import com.james.reggie_takeout.entity.Dish;
import com.james.reggie_takeout.entity.DishFlavor;
import com.james.reggie_takeout.mapper.DishMapper;
import com.james.reggie_takeout.service.DishFlavorService;
import com.james.reggie_takeout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */

    @Override
    @Transactional
    // 用事务来保证两张表的数据添加同成功同失败
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表 dish
        this.save(dishDto);


        // 获取已经保存好的 dishDto 刚刚 save 的 dishId
        Long dishId = dishDto.getId();

        // 处理 flavors 的 dishId 信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        // 保存菜品口味数据到菜品口味表 dish_flavor，批量 save 用 saveBatch
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {

        // 查询菜品基本信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        // 查询当前菜品的口味信息，从 dish_flavor 表中
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {

        // 更新 dish 表基本信息    由于 dishDto 是 dish 的子类，
        // 插入 dishDto 也会更新 dish 中的数据
        this.updateById(dishDto);

        // 如何更新 dish_flavor 表， 先清理当前菜品的口味数据   delete
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        // 再添加当前提交过来的口味数据   insert
        List<DishFlavor> flavors = dishDto.getFlavors();
        Long dishId = dishDto.getId();

        // 给 flavors 赋值 dishId
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }
}
