package com.gy.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
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

    @RequestMapping("redisTest")
    @ResponseBody
    public String testRedisson(){
        Jedis jedis = redisUtils.getJedis();
        //获取redisson锁
        RLock anyLock = redissonClient.getLock("anyLock");
        //加锁
        anyLock.lock();
        try{
            String sum = jedis.get("add");
            if(StringUtils.isBlank(sum)){
                sum="1";
            }
            jedis.set("add",(Integer.parseInt(sum)+1)+"");
            System.out.println("------>"+sum);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //释放锁
            anyLock.unlock();
            //关流
            jedis.close();
        }

        return "success";
    }
}
