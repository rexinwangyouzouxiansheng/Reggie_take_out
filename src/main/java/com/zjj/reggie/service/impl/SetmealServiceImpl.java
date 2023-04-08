package com.zjj.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.dto.SetmealDto;
import com.zjj.reggie.mapper.SetmealMapper;
import com.zjj.reggie.pojo.Category;
import com.zjj.reggie.pojo.Setmeal;
import com.zjj.reggie.pojo.SetmealDish;
import com.zjj.reggie.service.CategoryService;
import com.zjj.reggie.service.SetmealDishService;
import com.zjj.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @create: 2023-04-04 22:26
 * @author: Junj_Zou
 * @Description:
 */
@Service
@Slf4j
@Transactional
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealService setmealService;

    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    CategoryService categoryService;

    public Result<String> saveWithDeal(SetmealDto setmealDto){
        // 先保存套餐的基本信息
        setmealService.save(setmealDto);

        // 保存套餐和菜品联系  但由于SetmealDish里没有套餐id，所以要手动加上
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            Long setmealId = setmealDto.getId();
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        // 执行 保存套餐和菜品联系 操作
        setmealDishService.saveBatch(setmealDishes);
        return Result.success("添加套餐成功");
    }


    public Result<Page> page(int page, int pageSize, String name) {
        // 构造分页构造器
        Page<Setmeal> setmealPageInfo = new Page<>(page, pageSize);
        // 构造分页构造器
        Page<SetmealDto> setmealDtoPageInfo = new Page<>();

        // 构造条件筛选器
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(name != null, Setmeal::getName, name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);

        // 执行分页
        setmealService.page(setmealPageInfo,wrapper);

        // 但是，分页数据的records中的Setmeal缺少套餐品类名称，所以还是需要转成SetmealDto来传输

        // 复制除了records以外的分页对象
        BeanUtils.copyProperties(setmealPageInfo,setmealDtoPageInfo,"records");

        // 获取records
        List<Setmeal> records = setmealPageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;

        }).collect(Collectors.toList());

        setmealDtoPageInfo.setRecords(list);

        return Result.success(setmealDtoPageInfo);
    }

    public Result<String> deleteSetmeal(Long[] ids){
        int count = 0;
        for (Long id :ids) {
            Setmeal setmeal = setmealService.getById(id);
            Integer status = setmeal.getStatus();
            if (status == 0) {
                // 删除Setmeal中数据
                setmealService.removeById(id);
                // 删除SetmealDish中数据
                LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(SetmealDish::getSetmealId, id);
                setmealDishService.removeById(id);
            } else {
                count++;
                continue;
            }
        }

        if (count > 0) {
            return Result.error("套餐在售，请禁售后再删除");
        }

        return Result.success("删除套餐成功");
    }
}
