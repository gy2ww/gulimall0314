package com.gy.service;

import com.gy.bean.UmsMember;
import com.gy.bean.UmsMemberReceiveAddress;

import java.util.List;

/**
 * Created by gaoyong on 2020/3/15.
 */
public interface userService {
    /**
     * 查询所以用户信息
     * @return
     */
    List<UmsMember> getAllUser();

    /**
     * 根据用户id查询收货地址
     * @return
     * @param memberId
     */
    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(Long memberId);
}
