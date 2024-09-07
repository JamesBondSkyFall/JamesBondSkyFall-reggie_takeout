package com.james.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.james.reggie_takeout.entity.ComboDish;
import com.james.reggie_takeout.mapper.ComboDishMapper;
import com.james.reggie_takeout.mapper.ComboMapper;
import com.james.reggie_takeout.service.ComboDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ComboDishServiceImpl extends ServiceImpl<ComboDishMapper, ComboDish> implements ComboDishService {
}
