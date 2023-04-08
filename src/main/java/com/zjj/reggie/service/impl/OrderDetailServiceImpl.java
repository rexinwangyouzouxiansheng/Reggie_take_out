package com.zjj.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.reggie.mapper.OrderDetailMapper;
import com.zjj.reggie.mapper.OrdersMapper;
import com.zjj.reggie.pojo.OrderDetail;
import com.zjj.reggie.pojo.Orders;
import com.zjj.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @create: 2023-04-07 22:01
 * @author: Junj_Zou
 * @Description:
 */

@Slf4j
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
