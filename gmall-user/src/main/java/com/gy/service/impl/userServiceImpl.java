package com.gy.service.impl;

import com.gy.bean.UmsMember;
import com.gy.mapper.userMapper;
import com.gy.service.userService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by gaoyong on 2020/3/15.
 */
@Service
public class userServiceImpl implements userService {

    @Resource
    private userMapper userMapper;

    @Override
    public List<UmsMember> getAllUser() {
       List<UmsMember> umsMemberList =  userMapper.queryAllUser();
        return umsMemberList;
    }
}
