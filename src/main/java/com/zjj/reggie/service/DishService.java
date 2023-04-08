package com.zjj.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.dto.DishDto;
import com.zjj.reggie.pojo.Dish;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @create: 2023-04-04 22:23
 * @author: Junj_Zou
 * @Description:
 */
public interface DishService extends IService<Dish> {
    public Result<String> saveWithFlavor(DishDto dishDto);
    public Result<Page> pageDish(int page, int pageSize, String name);
    public Result<DishDto> getByIdWithFlavor(Long categoryId);
    public Result<String> updateWithFlavor(DishDto dishDto);
}
