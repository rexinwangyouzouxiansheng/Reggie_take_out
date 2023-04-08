package com.zjj.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.mapper.EmployeeMapper;
import com.zjj.reggie.pojo.Employee;
import com.zjj.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @create: 2023-04-02 22:10
 * @author: Junj_Zou
 * @Description:
 */

/**
 * 注意：但是 ServiceImpl 需要注入容器
 */
@Service
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    EmployeeService employeeService;

    /**
     * 关于这个处理逻辑，我认为具有可以改进的点
     *
     * @param employee
     * @return
     */
    public Result<Employee> login(Employee employee) {
        // 1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2、根据页面提交的用户名username查询数据库
        // 用到了 MyBatis-Plus 功能
        // 顺带提一句，这里用 getOne() 就能获取，因为 username 在数据库中有唯一性约束
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Employee> wrapper = employeeLambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(wrapper);

        // 3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return Result.error("查无此用户");
        }
        // 4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return Result.error("密码不正确");
        }
        // 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return Result.error("该员工已被禁用");
        }
        // 6、登录成功，将员工id存入Session并返回登录成功结果
        return Result.success(emp);
    }

    /**
     * 添加员工
     * username 具有唯一性约束，如果重复可能会抛出异常，所以要使用异常处理器进行全局异常捕获
     *
     * @param request
     * @param employee
     * @return
     */
    @Override
    public Result<String> addEmployee(HttpServletRequest request, Employee employee) {
        // 设置默认密码，也需要MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        // 设置创建/修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        // 设置创建/修改用户
//        Long empID = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empID);
//        employee.setUpdateUser(empID);

        // 通过service保存到数据库
        employeeService.save(employee);

        return Result.success("成功添加");
    }

    /**
     * 员工信息分页查询 (基于Mybatis-Plus分页插件
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Result<Page> pageEmployee(int page, int pageSize, String name) {
        // 构造分页构造器 存入 页码、每页条数
        Page pageInfo = new Page(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();
        // 添加查询条件(这里用的是模糊查询
        // 第一个参数是 判断某个条件是否成立，成立了才执行
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Employee::getUsername, name);
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询 会根据分页条件将查询到的数据存储到 pageInfo 中
        employeeService.page(pageInfo, lambdaQueryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 通用的修改员工信息 ---> 其实就是在已有update方法上，同步更新了employee里的updateTime和updateUser
     *
     * @param request
     * @param employee
     * @return
     */
    @Override
    public Result<String> updateEmployee(HttpServletRequest request, Employee employee) {

//        // 同步更新修改时间
//        employee.setUpdateTime(LocalDateTime.now());
//        // 同步更新修改人
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.updateById(employee);
        return Result.success("员工信息修改成功");
    }
}
