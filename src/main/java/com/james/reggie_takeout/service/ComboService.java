package com.james.reggie_takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.james.reggie_takeout.dto.ComboDto;
import com.james.reggie_takeout.entity.Combo;
import com.james.reggie_takeout.entity.ComboDish;

import java.util.List;

public interface ComboService extends IService<Combo> {

    /**
     * 新增套餐，同时需要插入数据到 套餐菜品关联表 combo_dish
     * @param comboDto
     */
    public void saveWithDish(ComboDto comboDto);

    /**
     * 删除套餐，同时删除 套餐菜品关联表 中对应的数据 combo_dish
     * @param
     */
    public void deleteWithDish(List<Long> ids);
}
