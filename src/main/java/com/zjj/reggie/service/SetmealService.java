package com.zjj.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.dto.SetmealDto;
import com.zjj.reggie.pojo.Setmeal;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @create: 2023-04-04 22:25
 * @author: Junj_Zou
 * @Description:
 */
public interface SetmealService extends IService<Setmeal> {
    public Result<String> saveWithDeal(SetmealDto setmealDto);
    public Result<Page> page(int page, int pageSize, String name);
    public Result<String> deleteSetmeal(Long[] ids);
}
