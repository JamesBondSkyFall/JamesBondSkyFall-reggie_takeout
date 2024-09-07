package com.james.reggie_takeout.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.james.reggie_takeout.common.CustomException;
import com.james.reggie_takeout.dto.ComboDto;
import com.james.reggie_takeout.entity.Combo;
import com.james.reggie_takeout.entity.ComboDish;
import com.james.reggie_takeout.mapper.ComboMapper;
import com.james.reggie_takeout.service.ComboDishService;
import com.james.reggie_takeout.service.ComboService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Provider;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ComboServiceImpl extends ServiceImpl<ComboMapper, Combo> implements ComboService {

    @Autowired
    private ComboDishService comboDishService;


    @Transactional
    public void saveWithDish(ComboDto comboDto) {



        // 保存套餐基本信息，到 combo 表
        this.save(comboDto);

        List<ComboDish> comboDishes = comboDto.getSetmealDishes();

        // 通过流的方式 将 comboId 赋值给 List<ComboDish> comboDishes
        // 中的 comboDish
        comboDishes.stream().map((item) -> {
            item.setSetmealId(comboDto.getId());
            return item;
        }).collect(Collectors.toList());


        // 保存套餐和菜品的关联信息
        comboDishService.saveBatch(comboDishes);

    }



    @Transactional
    public void deleteWithDish(List<Long> ids) {
        //先确定套餐是否是停售状态，是否可以删除，
        LambdaQueryWrapper<Combo> queryWrapper = new LambdaQueryWrapper<>();

        // 这两个查询条件判断是否要删除的套餐中有还在未停售状态的套餐，
        // 即 select count(*) from combo where id in {ids} and status = 1
        queryWrapper.in(Combo::getId, ids);
        queryWrapper.eq(Combo::getStatus, 1);

        //如果不能删除，抛出一个业务异常
        int count = this.count(queryWrapper);
        if(count > 0){
            throw new CustomException("Combo(s) you wanted to delete are still selling and cannot be deleted");
        }

        //如果可以，先删除套餐表中数据，
        this.removeByIds(ids);

        //然后删除 套餐和菜品关系表数据
        // 构造 queryWrapper , 因为套餐id ids 并不等于 关联表中的主键 id
        // delete * from combo_dish where combo_id in {ids}
        LambdaQueryWrapper<ComboDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ComboDish::getSetmealId, ids);

        comboDishService.remove(lambdaQueryWrapper);

    }
}
