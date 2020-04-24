package com.gy.api.service;

import com.gy.api.bean.UmsMember;
import com.gy.api.bean.UmsMemberReceiveAddress;

import java.util.List;

/**
 * Created by gaoyong on 2020/3/17.
 */
public interface userService {
    List<UmsMember> getAllUser();
    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(Long memberId);

    int modifyUserAddressDetail(Long memberId);

    /**
     * 验证登录
     * @param umsMember
     * @return
     */
    UmsMember loginCheck(UmsMember umsMember);

    /**
     * 把token存入redis
     * @param id
     * @param token
     */
    void pushCache(Long id, String token);
}
