package com.zjj.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.dto.DishDto;
import com.zjj.reggie.mapper.DishMapper;
import com.zjj.reggie.pojo.Category;
import com.zjj.reggie.pojo.Dish;
import com.zjj.reggie.pojo.DishFlavor;
import com.zjj.reggie.service.CategoryService;
import com.zjj.reggie.service.DishFlavorService;
import com.zjj.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @create: 2023-04-04 22:23
 * @author: Junj_Zou
 * @Description:
 */
@Service
@Slf4j
@Transactional // 事务注解
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;
    @Autowired
    DishService dishService;

    @Autowired
    CategoryService categoryService;

    /**
     * 保存菜品信息 包括口味信息
     * @param dishDto
     * @return
     */
    public Result<String> saveWithFlavor(DishDto dishDto) {
        // 保存菜品相关信息 ---> 看情况应该是 属性跟字段对应上 才赋值，对应不上就不赋值
        dishService.save(dishDto);

        // 保存菜品口味相关信息 ---> 需要在菜品口味中加入 菜品的id
        List<DishFlavor> flavors = dishDto.getFlavors();
        Long id = dishDto.getId();
        for (DishFlavor dishFlavor :flavors) {
            dishFlavor.setDishId(id);
        }
        dishFlavorService.saveBatch(flavors);
        return Result.success("菜品保存成功");
    }


    /**
     * 执行分页操作，但是还要传回 菜品名称
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    public Result<Page> pageDish(int page, int pageSize, String name) {
        // 构造分页构造器  // 这边使用 DishDto 而不是 Dish，原因在于 DishDto可以同时返回 菜品品类名称 属性
        Page<Dish> dishPageInfo = new Page<>(page, pageSize);
        // 构造两个分页构造器
        Page<DishDto> dishDtoPageInfo = new Page<>(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Dish::getName,name);
        wrapper.orderByDesc(Dish::getSort);

        // 执行查找分页
        dishService.page(dishPageInfo, wrapper);

        /**
         * 这里不单单是用Dish来做分页，还想往回传菜品名称，所以实际上传回的是 Page<DishDto>
         * 不过中间还要利用 菜品id 来查询菜品名称
         */

        // 复制Page对象数据
        BeanUtils.copyProperties(dishPageInfo,dishDtoPageInfo,"records");

        // 获取原records数据
        List<Dish> records = dishPageInfo.getRecords();
        //遍历每一条records数据
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //将数据赋给dishDto对象
            BeanUtils.copyProperties(item, dishDto);
            //然后获取一下dish对象的category_id属性
            Long categoryId = item.getCategoryId();  //分类id
            //根据这个属性，获取到Category对象（这里需要用@Autowired注入一个CategoryService对象）
            Category category = categoryService.getById(categoryId);
            String categoryName = null;
            if (category != null) {
                //随后获取Category对象的name属性，也就是菜品分类名称
                categoryName = category.getName();
            } else {
                categoryName = "暂无分类";
            }
            //最后将菜品分类名称赋给dishDto对象就好了
            dishDto.setCategoryName(categoryName);
            //结果返回一个dishDto对象
            return dishDto;
            //并将dishDto对象封装成一个集合，作为我们的最终结果
        }).collect(Collectors.toList());

        dishDtoPageInfo.setRecords(list);
        return Result.success(dishDtoPageInfo);
    }

    /**
     * 根据id获取菜品信息 包括口味信息
     * @param categoryId
     * @return
     */
    public Result<DishDto> getByIdWithFlavor(Long categoryId){
        // 根据id获取菜品信息
        Dish dish = dishService.getById(categoryId);
        // 创建一个dishDao对象
        DishDto dishDto = new DishDto();

        // 复制dish属性到dishDto
        BeanUtils.copyProperties(dish,dishDto);

        // 获取口味信息
        Long dishId = dish.getId();
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dishId);
        wrapper.orderByDesc(DishFlavor::getUpdateUser);

        List<DishFlavor> list = dishFlavorService.list(wrapper);
        dishDto.setFlavors(list);

        return Result.success(dishDto);
    }


    /**
     * 更新菜品信息 包括口味信息
     * 对于更新口味信息，其实就是 先删除 再重新加入
     * @param dishDto
     * @return
     */
    public Result<String> updateWithFlavor(DishDto dishDto) {
        // 更新菜品相关信息 ---> 看情况应该是 属性跟字段对应上 才赋值，对应不上就不赋值
        dishService.updateById(dishDto);

        // 对于口味信息，删了重加
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(wrapper);

        // 保存菜品口味相关信息 ---> 需要在菜品口味中加入 菜品的id
        List<DishFlavor> flavors = dishDto.getFlavors();
        Long id = dishDto.getId();
        for (DishFlavor dishFlavor :flavors) {
            dishFlavor.setDishId(id);
        }
        dishFlavorService.saveBatch(flavors);
        return Result.success("菜品更新成功");
    }
}
