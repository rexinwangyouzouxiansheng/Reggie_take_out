package com.zjj.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjj.reggie.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @create: 2023-04-02 22:04
 * @author: Junj_Zou
 * @Description:
 */

/**
 * mapper 一定要添加 @Mapper 注解才能够识别
 * 这里的简化都是基于 MyBatis-Plus
 * */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
