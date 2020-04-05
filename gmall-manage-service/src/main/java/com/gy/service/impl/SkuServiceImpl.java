package com.gy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gy.api.bean.*;
import com.gy.api.service.SkuService;
import com.gy.mapper.*;
import com.gy.util.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

/**
 * Created by gaoyong on 2020/3/28.
 */
@Service
public class SkuServiceImpl implements SkuService {

    public static final String KEY = "SKU:"+"INFO:";
    public static final String LOCK_KEY = "SKU:"+"LOCK";
    @Resource
    private PmsProductImageMapper pmsProductImageMapper;
    @Resource
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Resource
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;
    @Resource
    private PmsSkuInfoMapper pmsSkuInfoMapper;
    @Resource
    private PmsSkuImageMapper pmsSkuImageMapper;
    @Resource
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Resource
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Resource
    private RedisUtils redisUtils;
    @Override
    public List<PmsProductImage> spuImageList(String spuId) {

        Example example = new Example(PmsProductImage.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<PmsProductImage> pmsProductImageList = pmsProductImageMapper.selectByExample(example);
        return pmsProductImageList;
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {

        Example example = new Example(PmsProductSaleAttr.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductSaleAttrMapper.selectByExample(example);
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrList) {
            Example example1 = new Example(PmsProductSaleAttrValue.class);
            example1.createCriteria().andEqualTo("spuId",pmsProductSaleAttr.getSpuId()).andEqualTo("saleAttrId",pmsProductSaleAttr.getSaleAttrId());
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues1 = pmsProductSaleAttrValueMapper.selectByExample(example1);
            pmsProductSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues1);
        }

        return pmsProductSaleAttrList;
    }

    @Override
    public String saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

        int i=0;
        //添加sku主表
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        //添加skuImg
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
        //添加sku平台属性
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }
        //添加sku销售属性
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(pmsSkuInfo.getId());
            i = pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }
        if(i>0){
            return "success";
        }
        return "failed";
    }

    /**
     * 查询数据库中sku详情
     * @param skuId
     * @return
     */
    public PmsSkuInfo getSkuByDb(String skuId){
        //sku主表
        Example example = new Example(PmsSkuInfo.class);
        example.createCriteria().andEqualTo("id",skuId);
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoMapper.selectOneByExample(example);
        if(null!=pmsSkuInfo){
            //skuImage
            Example example1 = new Example(PmsSkuImage.class);
            example1.createCriteria().andEqualTo("skuId",skuId);
            List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.selectByExample(example1);
            pmsSkuInfo.setSkuImageList(pmsSkuImages);
            //skuattrvalue平台属性
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> pmsSkuAttrValueList = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValueList);
            //skusaleattrvalue平台销售属性
            PmsSkuSaleAttrValue pmsSkuSaleAttrValue = new PmsSkuSaleAttrValue();
            pmsSkuSaleAttrValue.setSkuId(skuId);
            List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList = pmsSkuSaleAttrValueMapper.select(pmsSkuSaleAttrValue);
            pmsSkuInfo.setSkuSaleAttrValueList(pmsSkuSaleAttrValueList);
        }
        return pmsSkuInfo;
    }
    @Override
    public PmsSkuInfo getSkuById(String skuId,String ip) {

        PmsSkuInfo pmsSkuInfo = null;
        Jedis jedis = redisUtils.getJedis();
        //组装redis的key
        String key = KEY+skuId;
        //根据key查询value
        String value = jedis.get(key);
        //如果redis中不为空就把从缓存中取出的值解析成json对象
        if(StringUtils.isNotEmpty(value)){
             pmsSkuInfo = JSON.parseObject(value, PmsSkuInfo.class);
             System.out.println("ip为"+ip+"的线程"+Thread.currentThread().getName()+"访问商品详情，redis中有值");
        }else{
            /*在高并发的情况下为了防止缓存击穿，我们利用redis的自带的分布式锁setnx来解决
            * 如果有大量请求直接访问数据库的话，肯定是不行的，数据库承受不住压力，所以在访问数据库
            * 之前让访问线程在redis进行一个setnx操作，如果成功才能访问数据库，如果不成功，就休眠几秒再自旋
            * setnx的特点就是当set的key不存在时才会成功，如果set的key存在就不成功
            */
            //生成一个随机uuid,用作lua脚本的传参
            String token = UUID.randomUUID().toString();
            System.out.println("ip为"+ip+"的线程"+Thread.currentThread().getName()+"访问商品详情"+"redis中无值，数据从数据库查"+"uuid="+token);
            String set = jedis.set(LOCK_KEY, token, "nx", "px", 10000);
            if(StringUtils.isNotBlank(set) && set.equals("OK")){
                //缓存为空就从数据库中取值，然后返回客户端并且把数据存入缓存
                System.out.println("ip为"+ip+"的线程"+Thread.currentThread().getName()+"获取锁成功，进入分布式锁");

                try {
                    Thread.sleep(9980);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pmsSkuInfo = getSkuByDb(skuId);
                if(null!=pmsSkuInfo){
                    //用JSON.toJSONString()解析对象时，对象不能为空，所以加了判断
                    System.out.println("ip为"+ip+"的线程"+Thread.currentThread().getName()+"发现数据库有值，将数据库中的值放入缓存");
                    jedis.set(key,JSON.toJSONString(pmsSkuInfo));
                }else{
                    //在实际的开发中，为了防止缓存穿透，就算数据库中没有对应的value，也可以设置一个null值或者空字符串加入缓存
                    //setex()为指定的 key 设置值及其过期时间。如果 key 已经存在， SETEX 命令将会替换旧的值。
                    jedis.setex(key,1000*60," ");
                    System.out.println("ip为"+ip+"的线程"+Thread.currentThread().getName()+"发现数据库中也没有值，在缓存中放了一个空字符串");
                }

                //处理完业务逻辑后，把set的key删掉，让别的线程有机会操作

                //lua脚本，防止在高并发的时候误删别的线程的key值,如果eval返回是1表示删除了自己的key,如果发现自己的key过期了，就不删除，防止删除别的线程的key,就返回0
                //用lua脚本在查询到key的同时删除key
                String script = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                Object eval = jedis.eval(script, Collections.singletonList(LOCK_KEY), Collections.singletonList(token));

               /* //判断要删除的key是不是自己线程的key，在高并发的场景下，没有lua脚本安全
                if(StringUtils.isNotBlank(jedis.get(LOCK_KEY)) && jedis.get(LOCK_KEY).equals(token)){
                    jedis.del(LOCK_KEY);
                }
                */
                System.out.println("eval="+JSON.toJSONString(eval));
                System.out.println("ip为"+ip+"的线程"+Thread.currentThread().getName()+"处理完，将锁归还……");
            }else{
                try {
                    System.out.println("ip为"+ip+"的线程"+Thread.currentThread().getName()+"没有获取到锁，进入休眠……");
                    Thread.sleep(50);
                    System.out.println("ip为"+ip+"的线程"+Thread.currentThread().getName()+"睡醒了……，继续查询商品详情,自旋……");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //TODO:这个地方必须注意，就是在获取锁失败后，自旋再次获取的时候，一定要写return,如果不写的话，相当于创建了一条新的线程，和原线程没有关系，写了return才是真正的自旋
                return getSkuById(skuId,ip);
            }
        }
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String spuId) {

        List<PmsSkuInfo> pmsSkuInfoList =  pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(spuId);
        return pmsSkuInfoList;
    }
}
