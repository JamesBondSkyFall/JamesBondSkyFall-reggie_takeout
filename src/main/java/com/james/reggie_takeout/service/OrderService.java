package com.james.reggie_takeout.service;

import cn.hutool.db.sql.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.james.reggie_takeout.entity.Orders;

public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);

}
