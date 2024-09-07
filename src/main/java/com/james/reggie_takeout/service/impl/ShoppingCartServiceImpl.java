package com.james.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.james.reggie_takeout.entity.ShoppingCart;
import com.james.reggie_takeout.mapper.ShopingCartMapper;
import com.james.reggie_takeout.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShopingCartMapper, ShoppingCart> implements ShoppingCartService{
}
