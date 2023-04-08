package com.zjj.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.reggie.pojo.Orders;

/**
 * @create: 2023-04-07 21:59
 * @author: Junj_Zou
 * @Description:
 */
public interface OrdersService extends IService<Orders> {
    public void submit(Orders orders);
}
