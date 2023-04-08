package com.zjj.reggie.dto;

import com.zjj.reggie.pojo.Dish;
import com.zjj.reggie.pojo.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @create: 2023-04-05 16:26
 * @author: Junj_Zou
 * @Description:
 */

/**
 * 说白了 DishDto就是个传输载体
 */
@Data
public class DishDto extends Dish {

    // 菜品口味
    private List<DishFlavor> flavors = new ArrayList<>();

    // 菜品品类名称
    private String categoryName;

    private Integer copies;
}
