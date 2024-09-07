package com.james.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.james.reggie_takeout.common.BaseContext;
import com.james.reggie_takeout.common.CustomException;
import com.james.reggie_takeout.entity.*;
import com.james.reggie_takeout.mapper.OrderMapper;
import com.james.reggie_takeout.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submit(Orders orders) {
        // 获取当前用户的 id
        Long currentId = BaseContext.getCurrentId();

        // 查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);

        if(shoppingCartList == null || shoppingCartList.size() == 0){
            throw new CustomException("shopping cart is empty and the order cannot be placed");
        }

        // 查询用户表和
        User user = userService.getById(currentId);

        // 地址表，以便插入信息
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new CustomException("addressBook information is incorrect and the order cannot be placed");
        }

        // 完成下单
        // 先设置 orders 的其他属性
        // 订单号
        long orderId = IdWorker.getId();

        // 保证在多线程的情况下也不会计算错误，线程安全的原子操作
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetailList =  shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2); // 待派送
        orders.setAmount(new BigDecimal(amount.get())); // 订单总金额，需要遍历购物车数据
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee()); // 收货人
        orders.setPhone(addressBook.getPhone()); // 收货人电话
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        //（向订单表插入一条数据）
        this.save(orders);

        // 向订单明细表插入数据，有可能是多条数据（订单中的每一项：菜品及套餐）
        orderDetailService.saveBatch(orderDetailList);

        // 清空购物车数据，因为已经下单完成
        shoppingCartService.remove(queryWrapper);
    }
}
