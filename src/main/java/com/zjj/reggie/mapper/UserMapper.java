package com.zjj.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjj.reggie.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @create: 2023-04-06 16:39
 * @author: Junj_Zou
 * @Description:
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
