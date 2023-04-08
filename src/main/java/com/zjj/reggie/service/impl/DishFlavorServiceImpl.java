package com.zjj.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.reggie.mapper.DishFlavorMapper;
import com.zjj.reggie.pojo.DishFlavor;
import com.zjj.reggie.service.DishFlavorService;
import com.zjj.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @create: 2023-04-05 15:41
 * @author: Junj_Zou
 * @Description:
 */
@Service
@Slf4j
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
