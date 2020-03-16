package com.gy.service.impl;

import com.gy.bean.UmsMember;
import com.gy.bean.UmsMemberReceiveAddress;
import com.gy.mapper.UserReceiveAddressMapper;
import com.gy.mapper.userMapper;
import com.gy.service.userService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by gaoyong on 2020/3/15.
 */
@Service
public class userServiceImpl implements userService {

    @Resource
    private userMapper userMapper;
    @Resource
    private UserReceiveAddressMapper userReceiveAddressMapper;

    @Override
    public List<UmsMember> getAllUser() {
       List<UmsMember> umsMemberList = null; // userMapper.queryAllUser();
        umsMemberList = userMapper.selectAll();
        return umsMemberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(Long memberId) {

        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo(memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = userReceiveAddressMapper.selectByExample(example);

        return umsMemberReceiveAddressList;
    }
}
