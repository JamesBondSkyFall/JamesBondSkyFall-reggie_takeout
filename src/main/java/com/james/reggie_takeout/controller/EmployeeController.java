package com.james.reggie_takeout.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.james.reggie_takeout.common.R;
import com.james.reggie_takeout.entity.Employee;
import com.james.reggie_takeout.service.EmployeeService;
//import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * employee login
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        // 1. 将页面提交的密码 password 进行 md5 加密处理， 提交的用户名，密码，已被封装到 employee 实体类中了
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2. 根据页面提交的 username 查询数据库
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeeLambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee one = employeeService.getOne(employeeLambdaQueryWrapper);

        // 3. 如果没有查询到则返回登录失败的结果
        if(one == null){
            return R.error("failed to login because of wrong username");
        }

        // 4. 密码比对，如果不一致则返回登录失败的结果
        if(!one.getPassword().equals(password)){
            return R.error("failed to login because of wong password");
        }

        // 5. 查看员工状态，如果为禁用状态，则返回员工已禁用结果
        if(one.getStatus() == 0){
            return R.error("failed to login because employee was disabled");
        }

        // 6. 登录成功，将员工 id 存入 session 并返回登录成功结果
        request.getSession().setAttribute("employee", one.getId());
        return R.success(one);

    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        // 清理session 中保存的当前登录员工的 id
        request.getSession().removeAttribute("employee");
        return R.success("log out successfully");

    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工，员工信息：{}", employee.toString());

        // 设置初始密码123456，需要进行 md5 加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        // 通过request对象，获取session，获取当前登录的用户id
        Long empId = (Long) request.getSession().getAttribute("employee");

        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);


        return R.success("Save employee successfully");
    }


    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        // 添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName,name);
        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据 ID 修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public  R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());

        long id = Thread.currentThread().getId();
        log.info("thread id: {}", id);

        //Long empId = (Long) request.getSession().getAttribute("employee");

        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("user information edited successfully");
    }

    /**
     * 根据 ID 来查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id){
        log.info("根据id来查询员工信息");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到该员工信息");
    }
}
