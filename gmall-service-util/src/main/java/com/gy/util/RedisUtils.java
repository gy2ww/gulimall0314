package com.gy.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


/**
 * Created by gaoyong on 2020/4/2.
 */
public class RedisUtils {

    private JedisPool jedisPool;

    /**
     * 将redis的池初始化到spring容器中
     */
    public void initPool(String host,int port,int database){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //最大连接数, 默认200个
        jedisPoolConfig.setMaxTotal(200);

        //最大空闲连接数, 默认30个
        jedisPoolConfig.setMaxIdle(30);
        //连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
        jedisPoolConfig.setBlockWhenExhausted(true);
        //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        jedisPoolConfig.setMaxWaitMillis(10*1000);
        //在获取连接的时候检查有效性,默认是false,s设置成true的话表示在调用一个jedis实例时，是否提前进行认证操作；如果为true，则得到的jedis实例均是可用的；
        jedisPoolConfig.setTestOnBorrow(true);
        //System.out.println("redis ip地址为="+host+"redis 端口号为="+port+"redis 数据库为="+database);
        jedisPool = new JedisPool(jedisPoolConfig,host,port,20*1000);
    }


    public Jedis getJedis(){
        //获取jedis的实例然后返回
        Jedis resource = jedisPool.getResource();
        return resource;
    }






























}
