package com.learning.lili.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.lili.common.BaseContext;
import com.learning.lili.common.R;
import com.learning.lili.entity.AddressBook;
import com.learning.lili.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
@Api(tags="用户地址管理")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增
     */
    @PostMapping
    @ApiOperation(value = "新增用户地址接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="addressBook", value="待新增的地址对象", required = true)
    })
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    @DeleteMapping
    @ApiOperation(value = "删除用户地址接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="ids", value="待删除地址的Id", required = true)
    })
    public R<String> delete(@RequestParam Long ids) {
        log.info("ids：{}", ids);
        addressBookService.removeById(ids);
        return R.success("删除地址成功");
    }


    @PutMapping
    @ApiOperation(value = "更新用户地址接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="addressBook", value="待更新的地址对象", required = true)
    })
    public R<String> update(@RequestBody AddressBook addressBook) {
        log.info("AddressBook：{}", addressBook);
        addressBookService.updateById(addressBook);
        return R.success("更新地址成功");
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    @ApiOperation(value = "设置用户默认地址接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="addressBook", value="待设置为默认地址的地址对象", required = true)
    })
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询用户地址接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="待查询地址的id", required = true)
    })
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    @ApiOperation(value = "查询用户默认地址接口")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询指定用户的全部地址接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="addressBook", value="待查询的地址对象", required = true)
    })
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(addressBookService.list(queryWrapper));
    }
}
