package com.james.reggie_takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.james.reggie_takeout.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    List<Employee> selectAll();

    Employee selectById(Long id);

    int insert(Employee employee);

    int update(Employee employee);

    int delete(Long id);
}
