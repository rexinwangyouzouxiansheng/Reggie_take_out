package com.zjj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.dto.SetmealDto;
import com.zjj.reggie.pojo.Setmeal;
import com.zjj.reggie.service.SetmealDishService;
import com.zjj.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @create: 2023-04-06 14:54
 * @author: Junj_Zou
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    @PostMapping
    //设置allEntries为true，清空缓存名称为setmealCache的所有缓存
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result<String> add(@RequestBody SetmealDto setmealDto) {
        Result<String> result = setmealService.saveWithDeal(setmealDto);
        return result;
    }

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, @Nullable String name) {
        Result<Page> result = setmealService.page(page, pageSize, name);
        return result;
    }

    @DeleteMapping()
    //设置allEntries为true，清空缓存名称为setmealCache的所有缓存
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result<String> delete(@RequestParam("ids") Long[] ids) {
        Result<String> result = setmealService.deleteSetmeal(ids);
        return result;
    }


    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public Result<List<Setmeal>> list(Setmeal setmeal) {
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, 1);
        //排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return Result.success(setmealList);
    }

}
