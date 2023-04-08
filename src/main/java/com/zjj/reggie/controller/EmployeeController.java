package com.zjj.reggie.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.pojo.Employee;
import com.zjj.reggie.service.EmployeeService;

import javax.servlet.http.HttpServletRequest;

/**
 * @create: 2023-04-02 22:12
 * @author: Junj_Zou
 * @Description:
 */


/**
 * 注解解释：
 * @Slf4j: 引入的log日志注解
 * @RestController: 由于没有引入thymeleaf，所以就让返回值直接返回到浏览器，而不是转发视图了
 * @RequestMapping("/employee"): 放在类上的目的是，想让这个类专门用来处理跟employee有关的请求
 *                              然后在下边的控制器方法种再分发具体请求
 *
 * */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 6、登录成功，将员工id存入Session并返回登录成功结果
        Result<Employee> result = employeeService.login(employee);
        if (result.getCode() == 1) { // 判定登录成功
            request.getSession().setAttribute("employee", result.getData().getId());
        }
        return result;
    }

    /**
     * 员工退出
     * 退出主要就是清理之前存在 session 里的 employee id。至于跳转到登录页面，前端会做
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    /**
     * 添加员工
     * @PostMapping: 页面发送的请求是 /employee，而控制器类上已经标注过这个了，所以这个地方就不用标了
     * @RequestBody: 页面传过来的是一个json格式的数据，在springboot里需要用@RequestBody来接收
     */
    @PostMapping
    public Result<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        Result<String> result = employeeService.addEmployee(request, employee);
        return result;
    }

    /**
     * 员工信息分页查询 (基于Mybatis-Plus分页插件
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, @Nullable String name) {
        log.info("page:{}, pageSize:{}, name:{}",page,pageSize,name);
        Result<Page> result = employeeService.pageEmployee(page, pageSize, name);
        return result;
    }

    /**
     * 通用的修改员工
     * @RequestBody ---> 因为页面发送过来的employee数据是
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public Result<String> update(HttpServletRequest request, @RequestBody Employee employee) {

        Result<String> result = employeeService.updateEmployee(request, employee);
        return result;
    }

    /**
     * 编辑员工信息时 回显数据
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result<Employee> view(@PathVariable("id") int id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return Result.success(employee);
        }
        return Result.error("查无此人");
    }

}
