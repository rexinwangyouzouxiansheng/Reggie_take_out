package com.zjj.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.pojo.Category;

/**
 * @create: 2023-04-04 20:08
 * @author: Junj_Zou
 * @Description:
 */
public interface CategoryService extends IService<Category> {
    public Result<Page> pageCategory(int page, int pageSize);
    public Result<String> removeCategory(Long id);
}
