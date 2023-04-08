package com.zjj.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.pojo.AddressBook;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @create: 2023-04-06 20:40
 * @author: Junj_Zou
 * @Description:
 */
public interface AddressBookService extends IService<AddressBook> {
    public Result<AddressBook> setDefaultAddress(AddressBook addressBook);
}
