package com.james.reggie_takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.james.reggie_takeout.common.BaseContext;
import com.james.reggie_takeout.entity.Combo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ComboMapper extends BaseMapper<Combo> {
}
