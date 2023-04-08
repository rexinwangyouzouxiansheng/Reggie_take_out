package com.zjj.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.reggie.common.BaseContext;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.mapper.AddressBookMapper;
import com.zjj.reggie.pojo.AddressBook;
import com.zjj.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @create: 2023-04-06 20:41
 * @author: Junj_Zou
 * @Description:
 */
@Service
@Slf4j
public class AddressBookServicelmpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Autowired
    AddressBookService addressBookService;

    public Result<AddressBook> setDefaultAddress(AddressBook addressBook) {
        // 获取userID
        Long userID = BaseContext.getCurrentID();
        // 有且仅有一个 ---> 先把该userID下所有isDefault字段设置成0，再把对应那个设置成1
        // 注意这里的条件构造器是LambdaUpdateWrapper，而不是我们前面经常用的LambdaQueryWrapper
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, userID);
        wrapper.set(AddressBook::getIsDefault,0);
        // 执行更新操作
        addressBookService.update(wrapper);
        // 设置默认地址
        addressBook.setIsDefault(1);
        // 再次执行更新操作
        addressBookService.updateById(addressBook);

        return Result.success(addressBook);
    }
}
