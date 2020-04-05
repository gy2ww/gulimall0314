package com.gy;


import com.alibaba.fastjson.JSON;
import com.gy.api.bean.PmsSkuInfo;
import com.gy.api.service.SkuService;
import com.gy.util.RedisUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageServiceApplicationTests{

    @Autowired
    private RedisUtils redisUtils;
    @Resource
    private SkuService skuService;
    @Test
    public void conntectloads(){
       /* Jedis jedis = null;
        try{
            jedis = redisUtils.getJedis();
            jedis.set("2","5");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.out.println("返回的结果是："+jedis);
            jedis.close();
        }*/
  /*      String skuId = "110";
        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId);
        System.out.println(JSON.toJSONString("pmsSkuInfo="+pmsSkuInfo));*/
    }
}