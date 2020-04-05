package com.gy.controller;

import com.gy.util.RedisUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;

/**
 * Created by gaoyong on 2020/4/5.
 */
@Controller
public class RedissonController {

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RedissonClient redissonClient;

    @RequestMapping("/testRedisson")
    @ResponseBody
    public String testRedisson(){
        Jedis jedis = redisUtils.getJedis();
        RLock anyLock = redissonClient.getLock("anyLock");
        return "success";
    }
}
