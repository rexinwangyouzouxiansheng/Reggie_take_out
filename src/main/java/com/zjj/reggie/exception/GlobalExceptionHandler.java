package com.zjj.reggie.exception;

/**
 * @create: 2023-04-03 22:14
 * @author: Junj_Zou
 * @Description:
 */


import com.zjj.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 * @ControllerAdvice 通知 注解
 * 如果使用@ConrtollerAdvice注解的话  方法体要加 @Requestbody 否则会报错
 * 如果使用@RestControllerAdvice注解 方法体不需要加@RequestBody
 */

// ---> 处理哪些控制器的异常 定义拦截规则
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody // 因为这里返回的是内容，而不是视图
@Slf4j
public class GlobalExceptionHandler {

    // ---> 处理哪类异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error("异常类型为：{}",ex.getMessage());
        // 判断异常信息中是否有 Duplicate entry  ---> 其实也就是判断是否违反唯一性约束
        if (ex.getMessage().contains("Duplicate entry") == true) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return Result.error(msg);

        }
         return Result.error("未知错误");
    }

    // ---> 处理哪类异常
    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException ex) {
        log.error("异常类型为：{}",ex.getMessage());

        String msg = ex.getMessage();

        return Result.error(msg);
    }
}
