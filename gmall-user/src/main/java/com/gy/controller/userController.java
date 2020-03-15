package com.gy.controller;

import com.gy.bean.UmsMember;
import com.gy.service.userService;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * Created by gaoyong on 2020/3/15.
 */
@RestController
@RequestMapping("/user")
public class userController {

    @Resource
    private userService userService;

    @RequestMapping("/getAllUser")
    @ResponseBody
    public List<UmsMember> getAllUser(){
      List<UmsMember> umsMembers=userService.getAllUser();

      return umsMembers;
    }
}
