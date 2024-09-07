package com.james.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.james.reggie_takeout.entity.Employee;
import com.james.reggie_takeout.mapper.EmployeeMapper;
import com.james.reggie_takeout.service.EmployeeService;
import org.springframework.stereotype.Service;


@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{
}
