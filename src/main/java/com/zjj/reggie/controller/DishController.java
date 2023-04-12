package com.zjj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.dto.DishDto;
import com.zjj.reggie.pojo.Dish;
import com.zjj.reggie.pojo.DishFlavor;
import com.zjj.reggie.service.DishFlavorService;
import com.zjj.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @create: 2023-04-05 15:42
 * @author: Junj_Zou
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    DishService dishService;

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 保存新增菜品数据
     * 表面上使用 dish 来做，实际上使用 dishDto 来做
     * DishDto 实际上就是 Dish 加了 Flavor 属性
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        log.info("接收的数据为:{}",dishDto);
        Result<String> result = dishService.saveWithFlavor(dishDto);
        // 清除redis缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return result;
    }

    /**
     * 分页管理
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, @Nullable String name) {
        Result<Page> result = dishService.pageDish(page, pageSize, name);
        return result;
    }

    @GetMapping("/{id}")
    public Result<DishDto> view(@PathVariable("id") Long categoryId) {
        Result<DishDto> result = dishService.getByIdWithFlavor(categoryId);
        return result;
    }

    /**
     * 更新菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) {
        Result<String> result = dishService.updateWithFlavor(dishDto);
        // 清除redis缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return result;
    }

    /**
     * 用于添加套餐中 回显某一菜品品类的所有菜品信息
     * 更新: 为了用户页面显示时能右"选择规格"，返回的数据应当包括flavor口味属性，所有 Dish ---> DishDto
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish) {
        // 添加redis缓存菜品数据
        // 先看能否在redis缓存中查询到相关菜品数据
        List<DishDto> dishDtoList;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        // 假如能在redis缓存中找到
        if (dishDtoList != null) {
            return Result.success(dishDtoList);
        }

        // 假如找不到，正常去数据库查
        // 取出当前菜品分类下所有在售的菜品
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getCategoryId,dish.getCategoryId());
        wrapper.eq(Dish::getStatus, 1);
        wrapper.orderByDesc(Dish::getSort);
        List<Dish> dishList = dishService.list(wrapper);

        // 将Dish转换成DishDto，并添加flavor口味属性
        dishDtoList = dishList.stream().map((item) ->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long dishID = dishDto.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(dishID != null, DishFlavor::getDishId, dishID);
            queryWrapper.orderByDesc(DishFlavor::getUpdateTime);
            List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(flavors);
            return dishDto;

        }).collect(Collectors.toList());

        // 将查询结果顺表保存到redis缓存中，设置存活时间60分钟
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return Result.success(dishDtoList);
    }


    /**
     * 批量修改菜品状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {
        log.info("status:{},ids:{}", status, ids);
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ids != null, Dish::getId, ids);
        updateWrapper.set(Dish::getStatus, status);

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Dish::getId, ids);
        List<Dish> dishes = dishService.list(lambdaQueryWrapper);
        for (Dish dish : dishes) {
            String key = "dish_" + dish.getCategoryId() + "_1";
            redisTemplate.delete(key);
        }

        dishService.update(updateWrapper);
        return Result.success("批量操作成功");
    }

}
