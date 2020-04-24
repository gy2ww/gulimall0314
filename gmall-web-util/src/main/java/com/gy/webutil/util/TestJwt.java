package com.gy.webutil.util;

import org.apache.tomcat.util.security.MD5Encoder;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by gaoyong on 2020/4/21.
 */
public class TestJwt {

    public static void main(String[] args) {

        Map<String, Object> map = new HashMap<>();
        map.put("memberId","C4CA4238A0B923820DCC509A6F75849B");
        map.put("userName","F66C583BB0F57FAF6EACAEDC7E30834A");
        String ip = "91D7352E39632DEFD0782C41E28D6527";
        String format = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date());
        String key = "4CA866602D19D4B1A57685071B16D03F";
        //jwt生成的token分为三部分，以.隔开，第一部分是公有部分，第二部分是私有部分，可以存储用户信息，第三部分是加密签名
        String encode = JwtUtil.encode(key, map, ip + format);
        System.out.println(encode);
    }
}
