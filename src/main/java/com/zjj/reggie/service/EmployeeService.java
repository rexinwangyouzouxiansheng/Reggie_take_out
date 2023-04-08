package com.zjj.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.pojo.Employee;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;

/**
 * @create: 2023-04-02 22:06
 * @author: Junj_Zou
 * @Description:
 */

/**
 * 注意：Service 接口本身不需要注入容器
 * */
public interface EmployeeService extends IService<Employee> {
    public Result<Employee> login(Employee employee);
    public Result<String> addEmployee(HttpServletRequest request, Employee employee);
    public Result<Page> pageEmployee(int page, int pageSize, @Nullable String name);
    public Result<String> updateEmployee(HttpServletRequest request, Employee employee);
}
