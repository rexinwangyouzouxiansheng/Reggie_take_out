package com.zjj.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.exception.CustomException;
import com.zjj.reggie.mapper.CategoryMapper;
import com.zjj.reggie.pojo.Category;
import com.zjj.reggie.pojo.Dish;
import com.zjj.reggie.pojo.Setmeal;
import com.zjj.reggie.service.CategoryService;
import com.zjj.reggie.service.DishService;
import com.zjj.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @create: 2023-04-04 20:09
 * @author: Junj_Zou
 * @Description:
 */
@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    CategoryService categoryService;

    /**
     * 分类 分页功能实现
     * @param page
     * @param pageSize
     * @return
     */
    public Result<Page> pageCategory(int page, int pageSize) {
        // 构造分页构造器
        Page<Category> pageInfo = new Page(page, pageSize);

        // 构造条件查询器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper();
        // 添加排序条件
        lambdaQueryWrapper.orderByDesc(Category::getSort);

        // 分页查询
        categoryService.page(pageInfo,lambdaQueryWrapper);
        return Result.success(pageInfo);
    }

    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;

    /**
     * 删除分类，但是删除之前需要检测一下该分类id是否关联菜品或者套餐
     * @param id
     * @return
     */
    public Result<String> removeCategory(Long id) {
        // 判断该分类id是否关联菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if (count1 > 0) {
            // 假如在菜品表里能找到相关分类，说明不可删除，抛出自定义异常
            throw new CustomException("当前分类与菜品关联，不可删除");

        }

        // 判断该分类id是否关联套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0) {
            // 假如在套餐表里能找到相关分类，说明不可删除，抛出自定义异常
            throw new CustomException("当前分类与套餐关联，不可删除");
        }

        // 正常执行删除操作
        categoryService.removeById(id);

        return Result.success("删除成功");
    }


}
