package com.james.reggie_takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.james.reggie_takeout.common.R;
import com.james.reggie_takeout.dto.ComboDto;
import com.james.reggie_takeout.entity.Category;
import com.james.reggie_takeout.entity.Combo;
import com.james.reggie_takeout.service.CategoryService;
import com.james.reggie_takeout.service.ComboDishService;
import com.james.reggie_takeout.service.ComboService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class ComboController {

    @Autowired
    private ComboService comboService;

    @Autowired
    private ComboDishService comboDishService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐
     * @param comboDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody ComboDto comboDto){
        log.info("套餐信息: ",comboDto);

        // 验证 ComboDishes 是否为空
        if (comboDto.getSetmealDishes() == null || comboDto.getSetmealDishes().isEmpty()) {
            log.error("Combo dishes are null or empty");
            return R.error("Combo dishes cannot be null or empty");
        }


        comboService.saveWithDish(comboDto);

        return R.success("combo saved successfully");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        // 构造分页构造器对象
        Page<Combo> pageInfo = new Page<>(page, pageSize);
        Page<ComboDto> dtoPage = new Page<>();


        LambdaQueryWrapper<Combo> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件，根据 name 进行 like 模糊查询
        lambdaQueryWrapper.like(name != null, Combo::getName, name);

        // 添加排序条件，根据更新时间降序排列
        lambdaQueryWrapper.orderByDesc(Combo::getUpdateTime);


        comboService.page(pageInfo, lambdaQueryWrapper);

        // 进行对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Combo> records = pageInfo.getRecords();

        List<ComboDto> list = records.stream().map((item) -> {
                    ComboDto comboDto = new ComboDto();
                    // 进行拷贝
                    BeanUtils.copyProperties(item, comboDto);
                    Long categoryId = item.getCategoryId();
                    // 根据分类 id 来查询 category 对象，来查询到 categoryName
                    Category category = categoryService.getById(categoryId);
                    if(category != null){
                        String categoryName = category.getName();
                        comboDto.setCategoryName(categoryName);
                    }
                    return comboDto;
                }
                ).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        log.info("ids:{}", ids);

        comboService.deleteWithDish(ids);
        return R.success("combo(s) deleted successfully");
    }

    /**
     * 根据条件查询套餐数据  id 和 status
     * @param combo
     * @return
     */
    @GetMapping("/list")
    public R<List<Combo>> list (Combo combo){
        LambdaQueryWrapper<Combo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(combo.getCategoryId() != null, Combo::getCategoryId, combo.getCategoryId());
        queryWrapper.eq(combo.getStatus() != null, Combo::getStatus, combo.getStatus());
        queryWrapper.orderByDesc(Combo::getUpdateTime);
        List<Combo> comboList = comboService.list(queryWrapper);

        return R.success(comboList);
    }

}
