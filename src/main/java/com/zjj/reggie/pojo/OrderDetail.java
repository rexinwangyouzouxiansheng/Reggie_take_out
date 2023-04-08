package com.zjj.reggie.pojo;

/**
 * @create: 2023-04-07 21:56
 * @author: Junj_Zou
 * @Description:
 */

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细
 */
@Data
public class OrderDetail {
    private static final long serialVersionUID = 1L;

    private Long id;

    //名称
    private String name;

    //订单id
    private Long orderId;


    //菜品id
    private Long dishId;


    //套餐id
    private Long setmealId;


    //口味
    private String dishFlavor;


    //数量
    private Integer number;

    //金额
    private BigDecimal amount;

    //图片
    private String image;
}
