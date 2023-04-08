package com.zjj.reggie.dto;

import com.zjj.reggie.pojo.Setmeal;
import com.zjj.reggie.pojo.SetmealDish;
import lombok.Data;

import java.util.List;

/**
 * @create: 2023-04-06 14:53
 * @author: Junj_Zou
 * @Description:
 */

/**
 * 作为一个套餐，里边有哪些菜品没说，套餐品类也没说，这些都要加上
 */
@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
