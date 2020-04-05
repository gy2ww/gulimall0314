package com.gy.config;

import com.gy.util.RedisUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by gaoyong on 2020/4/2.
 */
@Configuration
public class RedisConfig {

    //这个值一般写死就行，这个注解的作用就是读取配置文件里的值
    @Value("${spring.redis.host:disabled}")
    private String host;
    @Value("${spring.redis.port:0}")
    private int port;
    @Value("${spring.redis.database:0}")
    private int database;

    /**
     * 这个@Bean就是把这个方法交给springIOC来管理和维护，这个注解和@Configuration注解一起用
     * @return
     */
    @Bean
    public RedisUtils redisUtil(){

        if(host.equals("disabled")){
            return null;
        }
        RedisUtils redisUtils = new RedisUtils();
        //System.out.println("ip地址为="+host+"端口号为="+port+"数据库为="+database);
        redisUtils.initPool(host,port,database);
        return redisUtils;

    }
}
