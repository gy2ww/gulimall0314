package com.gy.controller;

import com.alibaba.fastjson.JSON;
import com.gy.util.HttpClientUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gaoyong on 2020/4/25.
 */

public class TestAccessToken {

    public static void main(String[] args) {

        //1、用户请求授权，会返回一个授权码
        String url = "https://api.weibo.com/oauth2/authorize?client_id=1826296733&response_type=code&redirect_uri=http://passport.gmall.com:8071/vlogin";

       // String s = HttpClientUtil.doGet(url);
        //返回的授权码   http://passport.gmall.com:8071/vlogin?code=4b2fb4b9403575f5c79ffceb560c6896
        String sqm = "4b2fb4b9403575f5c79ffceb560c6896";



        //2、用授权码去换取access_token,这个必须是post请求
        String aturl = "https://api.weibo.com/oauth2/access_token?";
        Map<String, String> map = new HashMap<>();
        //APPKEY
        map.put("client_id","1826296733");
        //APP SECRET
        map.put("client_secret","82bd1fd39ea82477d9929d9e7cf9a660");
        //回调地址
        map.put("redirect_uri","http://passport.gmall.com:8071/vlogin");
        //授权码
        map.put("code",sqm);
       /* String s1 = HttpClientUtil.doPost(aturl, map);
        Map<String,Object> map1 = JSON.parseObject(s1, Map.class);
        Object access_token = map1.get("access_token");*/
        //Object access_token = 2.0011UoiHvMxazB8de2f296301V_ZIC


        //3、根据access_token去查询用户信息
        String userUrl = "https://api.weibo.com/2/users/show.json?access_token=2.0011UoiHvMxazB8de2f296301V_ZIC&uid=1";

        String s = HttpClientUtil.doGet(userUrl);
        Map map1 = JSON.parseObject(s, Map.class);
        map1.forEach((key,value)->{
            System.out.println(key+"  :   "+value);
        });
    }
}
