package com.zjj.reggie.controller;

import com.zjj.reggie.common.Result;
import com.zjj.reggie.pojo.Orders;
import com.zjj.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @create: 2023-04-07 22:03
 * @author: Junj_Zou
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    OrdersService ordersService;

    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders) {
        log.info("orders:{}", orders);
//        ordersService.submit(orders);
        return Result.success("用户下单成功");
    }

}
