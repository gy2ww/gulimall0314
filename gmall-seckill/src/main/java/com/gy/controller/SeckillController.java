package com.gy.controller;

import com.gy.util.RedisUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * Created by gaoyong on 2020/5/18.
 */
@Controller
public class SeckillController {

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RedissonClient redissonClient;


    /**
     * 利用Redisson，这种解法是先到先得,他跟用watch不一样，这个是在同一时间可能有n个同时秒杀成功，他可以保证库存不被卖超
     * @return
     */
    @RequestMapping("/seckill2")
    @ResponseBody
    public String seckill2(){

        Jedis jedis = redisUtils.getJedis();
        String uuid = UUID.randomUUID().toString();
        //利用Redisson的机制实现秒杀
        RSemaphore semaphore = redissonClient.getSemaphore("sku106");
        boolean b = semaphore.tryAcquire();
        Integer stock = Integer.parseInt(jedis.get("sku106"));
        if(b){
            System.out.println(uuid+"秒杀成功，第"+(100-stock)+"件商品被秒杀成功,当前商品剩余量是："+stock+",  当前时间是："+System.currentTimeMillis());
            System.out.println("给订单系统发送消息");
        }else{
            System.out.println("秒杀失败");
        }
        jedis.close();
        return "success";
    }
    /**
     * 这种形式的秒杀是随机的，说白了就是比运气
     * @return
     */
    @RequestMapping("/seckill")
    @ResponseBody
    public String secKill(){
        String uuid = UUID.randomUUID().toString();
        //获取秒杀商品的库存
        Jedis jedis = redisUtils.getJedis();

        //开启redis的监听机制，监听商品的数量，比如说有一个用户正在秒杀，获取了商品库存是8，
        // 但是在执行过程中有别的用户对这个数量进行了修改，如果开启了监听，发现数量和自己一开始获取的不一样，
        // 发生了改变，就会返回null
        jedis.watch("sku106");
        //如果库存小于0就不能秒杀，会发生超卖
        Integer stock = Integer.parseInt(jedis.get("sku106"));
        if(stock > 0){
            //开启redis事务
            Transaction multi = jedis.multi();
            //执行减库存操作
            multi.incrBy("sku106",-1);
            //提交事务
            List<Object> exec = multi.exec();
            if(exec != null && exec.size()>0){
                System.out.println(uuid+"秒杀成功，第"+(100-stock)+"件商品被秒杀成功,当前商品剩余量是："+stock);
                System.out.println("给订单系统发送消息");
            }else{
                System.out.println("秒杀失败");
            }
        }else{
            System.out.println("库存已清空，秒杀失败");
        }
        jedis.close();
        return "success";
    }
}
