package com.zjj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.pojo.Category;
import com.zjj.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @create: 2023-04-04 20:11
 * @author: Junj_Zou
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    /**
     * 保存 菜品/套餐 分类
     * 都是同一个路径
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody Category category){
        log.info("接收到了分类的请求");
        categoryService.save(category);
        return Result.success(category.getType() == 1 ? "新增菜品分类成功" :"新增套餐分类成功");
    }


    /**
     * 分类 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize) {
        Result<Page> result = categoryService.pageCategory(page, pageSize);
        return result;
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping()
    public Result<String> delete(Long id) {
        log.info("将被删除的分类是: {}",id);
        Result<String> result = categoryService.removeCategory(id);
        return result;
    }

    /**
     * 通用的更新操作
     * @param category
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Category category) {
        log.info("修改分类信息为：{}", category);
        categoryService.updateById(category);
        return Result.success("修改分类信息成功");
    }

    /**
     * 新增菜品中用于返回菜品品类列表
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Category category) {
        // 条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        // 添加条件 type: 1是菜品 2是套餐
        wrapper.eq(category.getType() != null, Category::getType, category.getType());
        // 排序 sort升序 updateTime降序
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        // 获取列表
        List<Category> list = categoryService.list(wrapper);

        return Result.success(list);
    }

}
