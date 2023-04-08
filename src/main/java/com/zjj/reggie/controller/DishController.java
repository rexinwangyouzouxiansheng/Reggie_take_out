package com.zjj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
        return result;
    }

    /**
     * 用于添加套餐中 回显某一菜品品类的所有菜品信息
     * 更新: 为了用户页面显示时能右"选择规格"，返回的数据应当包括flavor口味属性，所有 Dish ---> DishDto
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishDto>> list(Long categoryId) {
        // 取出当前菜品分类下所有在售的菜品
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getCategoryId,categoryId);
        wrapper.eq(Dish::getStatus, 1);
        wrapper.orderByDesc(Dish::getSort);
        List<Dish> dishList = dishService.list(wrapper);

        // 将Dish转换成DishDto，并添加flavor口味属性
        List<DishDto> dishDtoList = dishList.stream().map((item) ->{
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


        return Result.success(dishDtoList);
    }

}
