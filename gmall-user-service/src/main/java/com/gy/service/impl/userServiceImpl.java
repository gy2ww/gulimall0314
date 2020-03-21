package com.gy.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.gy.api.bean.UmsMember;
import com.gy.api.bean.UmsMemberReceiveAddress;
import com.gy.api.service.userService;
import com.gy.mapper.userMapper;
import com.gy.mapper.UserReceiveAddressMapper;
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
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses =  userReceiveAddressMapper.selectByExample(example);

        return umsMemberReceiveAddresses;
    }

    @Override
    public int modifyUserAddressDetail(Long id) {
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("id",id);
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setProvince("河北省");
        umsMemberReceiveAddress.setCity("衡水市");
        umsMemberReceiveAddress.setName("高大帅");
        umsMemberReceiveAddress.setRegion("桃城区");
        umsMemberReceiveAddress.setDetailAddress("北方工业园");
        int i = userReceiveAddressMapper.updateByExampleSelective(umsMemberReceiveAddress, example);
        return i;
    }
}
