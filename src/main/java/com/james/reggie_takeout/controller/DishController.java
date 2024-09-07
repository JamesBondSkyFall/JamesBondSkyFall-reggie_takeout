package com.james.reggie_takeout.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.james.reggie_takeout.common.R;
import com.james.reggie_takeout.dto.DishDto;
import com.james.reggie_takeout.entity.Category;
import com.james.reggie_takeout.entity.Dish;
import com.james.reggie_takeout.entity.DishFlavor;
import com.james.reggie_takeout.service.CategoryService;
import com.james.reggie_takeout.service.DishFlavorService;
import com.james.reggie_takeout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 *
 */


@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){

        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);
        return R.success("dish saved successfully");
    }


    /**
     * 菜品信息的分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        // 构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        // 添加过滤条件, like - 模糊查询
        queryWrapper.like(name != null, Dish::getName, name);

        // 添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        // 执行分页查询
        dishService.page(pageInfo,queryWrapper);

        // 对象拷贝，将 pageInfo 中的值赋值给 dishDtoPage
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        // 处理 records
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            // 根据 categoryId 查询 category 对象，
            // 然后获取 name，再赋值到 dishDto
            Category category = categoryService.getById(categoryId);

            // 如果 category 不为空，再继续后面的步骤
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
                }
        ).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }


    /**
     * 根据 id 查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);



        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("dish updated successfully");
    }

    /**
     * 根据条件查询对应的菜品数据
     * 此处为 P72 首次讲到，不使用 Long categoryId 是因为，
     * 用 dish 这个对象也能接受到 categoryId
     * @param dish
     * @return
     */
    /*
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){

        // 构造查询条件对象
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());

        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        // 只查询菜品 status 为 1 的，即未停售的菜品
        queryWrapper.eq(Dish::getStatus, 1);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }

     */


    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        // 构造查询条件对象
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());

        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        // 只查询菜品 status 为 1 的，即未停售的菜品
        queryWrapper.eq(Dish::getStatus, 1);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
                    DishDto dishDto = new DishDto();
                    BeanUtils.copyProperties(item, dishDto);
                    Long categoryId = item.getCategoryId();
                    // 根据 categoryId 查询 category 对象，
                    // 然后获取 name，再赋值到 dishDto
                    Category category = categoryService.getById(categoryId);

                    // 如果 category 不为空，再继续后面的步骤
                    if(category != null){
                        String categoryName = category.getName();
                        dishDto.setCategoryName(categoryName);
                    }
                    // 当前菜品 id
                    Long dishId = item.getId();
                    LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                    lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
                    // select * from dish_flavor where dish_id = ?
                    List<DishFlavor> flavorList = dishFlavorService.list(lambdaQueryWrapper);
                    dishDto.setFlavors(flavorList);

            return dishDto;
                }
        ).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

}
