package com.gy.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gy.api.bean.UmsMember;
import com.gy.api.service.userService;
import com.gy.util.MD5Util;
import com.gy.webutil.util.JwtUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by gaoyong on 2020/4/21.
 */
@Controller
public class PassPortController {


    @Reference
    private userService userService;

    @RequestMapping("/verify")
    @ResponseBody
    public String verify(String token,String currentIp){

        Map<String, String> resultMap = new HashMap<>();
        //jwt解码token
        String keys = MD5Util.md5Encrypt32Lower("gmall0314");
        Map<String, Object> map = JwtUtil.decode(token, keys, currentIp);
        //如果jwt解码出来map不为空，就表示验证通过
        if(map!=null){
           resultMap.put("status","success");
           resultMap.put("memberId", String.valueOf(map.get("memberId")));
           resultMap.put("password", String.valueOf(map.get("password")));

        }else{
            resultMap.put("status","failed");
        }
        return JSON.toJSONString(resultMap);
    }
    @RequestMapping("/login")
    @ResponseBody
    public String login(HttpServletRequest request,UmsMember umsMember){

        String token = Strings.EMPTY;
       //根据用户名和密码验证用户的登录
       UmsMember umsMember1 = userService.loginCheck(umsMember);
       if(null!=umsMember1){
           System.out.println("用户名密码正确");
           //调用jwt生成token
           //组装jwt需要的参数
           //jwt私有部分，用户信息
           HashMap<String, Object> map = new HashMap<>();
           map.put("memberId",umsMember1.getId());
           map.put("password",umsMember1.getPassword());
           //jwt第三部分盐值的设置
           String ip = Strings.EMPTY;
           String keyName = Strings.EMPTY;
           ip = request.getHeader("x-forwarded-for");//获取nginx转发的原ip
           if(StringUtils.isBlank(ip)){
               //如果这个请求没有通过nginx进行转发，就直接获取ip就行
               ip = request.getRemoteAddr();
               String key =  "ipName";
               String key1 = "keyName";
               String url = "D:\\puhui-gitspace\\gulimall0314\\gmall-passport-web\\src\\main\\resources\\application.properties";
               try {
                   keyName = getIpAdress(url,key1);
               } catch (IOException e) {
                   e.printStackTrace();
               }
               if(StringUtils.isBlank(ip)){
                   //一般情况下一个请求是不可能没有ip地址的，否则是非法请求，如果上边的请求没有ip，就从配置文件中读取
                                   try {
                       ip = getIpAdress(url,key);

                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           }
           //ip加密
            ip = MD5Util.md5Encrypt32Lower(ip);
           //key加密
            keyName = MD5Util.md5Encrypt32Lower(keyName);
           //jwt生成token
            token = JwtUtil.encode(keyName, map, ip);
           //把生成的token存入redis
           userService.pushCache(umsMember1.getId(),token);

       }else{
         //用户名密码不正确，返回给前端一个标识
           return "failed";
       }

        return token;
    }

    private String getIpAdress(String url, String key) throws IOException {

        String ip = Strings.EMPTY;
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        BufferedReader bufferedReader = new BufferedReader(new FileReader(url));
        properties.load(bufferedReader);
        // 获取key对应的value值
        ip = properties.getProperty(key);
        return ip;
    }

    @RequestMapping("/index")
    public String index(String ReturnUrl,ModelMap map){


        map.put("ReturnUrl",ReturnUrl);

        return "index";
    }
}
