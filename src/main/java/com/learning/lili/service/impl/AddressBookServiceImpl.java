package com.learning.lili.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.lili.entity.AddressBook;
import com.learning.lili.mapper.AddressBookMapper;
import com.learning.lili.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
