package com.zjj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjj.reggie.common.BaseContext;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.pojo.ShoppingCart;
import com.zjj.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @create: 2023-04-07 14:44
 * @author: Junj_Zou
 * @Description:
 */

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        // 用户id，数量 要额外操作; 口味要额外保存
        // 获取/设置用户id
        Long userID = BaseContext.getCurrentID();
        shoppingCart.setUserId(userID);

        // 判断当前菜品/套餐是否已在购物车中
        LambdaQueryWrapper<ShoppingCart> wapper = new LambdaQueryWrapper<>();
        // 不管是菜品还是套餐都可以查，根据条件来查就行
        wapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
        wapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        // 获取一个
        ShoppingCart cartServiceOne = shoppingCartService.getOne(wapper);

        // 判断当前菜品/套餐是否已在购物车中
        if (cartServiceOne != null) {
            // 如果已经存在，数量+1
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            // 如果还未存在，则添加到数据库，默认数量为1
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            // 方便返回结果
            cartServiceOne = shoppingCart;
        }

        return Result.success(cartServiceOne);
    }

    /**
     * 数据回显，展示当前用户购物车中所有数据
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        Long userId = BaseContext.getCurrentID();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        return Result.success(shoppingCarts);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public Result<String> clean() {
        //条件构造器
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //获取当前用户id
        Long userId = BaseContext.getCurrentID();
        queryWrapper.eq(userId != null, ShoppingCart::getUserId, userId);
        //删除当前用户id的所有购物车数据
        shoppingCartService.remove(queryWrapper);
        return Result.success("成功清空购物车");
    }

}
