package com.zjj.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.reggie.mapper.ShoppingCartMapper;
import com.zjj.reggie.pojo.ShoppingCart;
import com.zjj.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @create: 2023-04-07 14:43
 * @author: Junj_Zou
 * @Description:
 */
@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
