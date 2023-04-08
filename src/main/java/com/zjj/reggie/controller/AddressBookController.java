package com.zjj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjj.reggie.common.BaseContext;
import com.zjj.reggie.common.Result;
import com.zjj.reggie.pojo.AddressBook;
import com.zjj.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @create: 2023-04-06 20:42
 * @author: Junj_Zou
 * @Description:
 */

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    AddressBookService addressBookService;

    /**
     * 回显地址簿
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentID());
        log.info("addressBook={}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> addressBooks = addressBookService.list(queryWrapper);
        return Result.success(addressBooks);
    }

    @PostMapping
    public Result<AddressBook> addAddress(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentID());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return Result.success(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public Result<AddressBook> setDefaultAddress(@RequestBody AddressBook addressBook) {
        Result<AddressBook> result = addressBookService.setDefaultAddress(addressBook);
        return result;
    }

    /**
     * 根据用户id获取默认地址
     * @return
     */
    @GetMapping("/default")
    public Result<AddressBook> getDefaultAddress() {
        Long userID = BaseContext.getCurrentID();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        // 对上userID
        wrapper.eq(AddressBook::getUserId,userID);
        // 对上默认地址
        wrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(wrapper);
        return Result.success(addressBook);
    }

}
