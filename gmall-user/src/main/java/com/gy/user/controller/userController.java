package com.gy.user.controller;

import com.gy.api.bean.UmsMemberReceiveAddress;
import com.gy.api.bean.UmsMember;
import com.gy.api.service.userService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by gaoyong on 2020/3/15.
 */
@RestController
@RequestMapping("/user")
public class userController {

    @Resource
    private userService userService;

    @RequestMapping("/getReceiveAddress")
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(Long MemberId){
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = userService.getReceiveAddressByMemberId(MemberId);
        return umsMemberReceiveAddressList;
    }
    @RequestMapping("/getAllUser")
    @ResponseBody
    public List<UmsMember> getAllUser(){
      List<UmsMember> umsMembers=userService.getAllUser();
      return umsMembers;
    }
    @RequestMapping("/modifyUser")
    public boolean modifyUserAddressDetail(Long id){
        int i = userService.modifyUserAddressDetail(id);
        if(i>0){
            return true;
        }
        return false;
    }
}
