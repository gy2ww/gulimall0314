package com.gy.webutil.interrupt;

import com.alibaba.fastjson.JSON;
import com.gy.util.HttpClientUtil;
import com.gy.util.MD5Util;
import com.gy.webutil.annotations.NeedLogin;
import com.gy.webutil.util.CookieUtil;
import com.gy.webutil.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gaoyong on 2020/4/21.
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //System.out.println("进来了");

        //判断这个请求需不需要拦截
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            NeedLogin annotation = hm.getMethodAnnotation(NeedLogin.class);
            //请求的这个方法上没有这个注解，表示不用验证登录也可以访问
            if (annotation == null) {
                return true;
            }
            //如果这个方法上有这个注解，还要判断这个注解中的参数是true还是false，如果是true表示必须登录成功才能访问，如果是false，表示不登录或者登录失效也可以访问
            boolean bol = annotation.LoginSucess();

            String token = Strings.EMPTY;
            //cookie中的token可能是用户之前登录过存入cookie中的，所以是oldToken
            String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);

            if(StringUtils.isNotBlank(oldToken)){
                token = oldToken;
            }
            //当前用户请求携带的token
            String newToken = request.getParameter("token");
            if(StringUtils.isNotBlank(newToken)){
                token = newToken;
            }
            String success = Strings.EMPTY;
            String ip = Strings.EMPTY;

            Map<String, String> resultmap = new HashMap<>();
            if(StringUtils.isNotBlank(token)){
                ip = request.getHeader("x-forwarded-for");//获取nginx转发的原ip
                if(StringUtils.isBlank(ip)){
                    //如果这个请求没有通过nginx进行转发，就直接获取ip就行
                    ip = request.getRemoteAddr();
                    if(com.alibaba.dubbo.common.utils.StringUtils.isBlank(ip)){
                        //一般情况下一个请求是不可能没有ip地址的，否则是非法请求
                        ip = "127.0.0.1";
                    }
                }
                //ip加密
                ip = MD5Util.md5Encrypt32Lower(ip);
                //通过http请求去认证中心验证token是否有效
                String successJson = HttpClientUtil.doGet("http://passport.gmall.com:8071/verify?token="+token+"&currentIp="+ip);
                resultmap = JSON.parseObject(successJson, Map.class);

                //获取状态
                success = resultmap.get("status");

            }
            //bol为true，表示这个方法必须登录成功才能访问
            if (bol) {
                if(!success.equals("success")){
                    //token无效，说明登录已过期，返回认证中心进行重新登录
                    StringBuffer requestURL = request.getRequestURL();
                    //重定向到认证中心登录
                    response.sendRedirect("http://passport.gmall.com:8071/index?ReturnUrl="+requestURL);
                    return false;
                }
             //把token中的用户信息存入request
                request.setAttribute("memberId",resultmap.get("memberId"));
                request.setAttribute("password",resultmap.get("password"));
            } else {
               //不登录也可以访问，但是也要验证，如果用户刚登录或者以前登录过，就把token中的用户信息存入request
                //个人感觉这个地方不用判断success返回的是成功或失败，只有token不为空，就把token中的用户信息放入request
                if(success.equals("success")){
                    request.setAttribute("memberId",resultmap.get("memberId"));
                    request.setAttribute("password",resultmap.get("password"));
                }
            }

            if(StringUtils.isNotBlank(token)){
                //token有效，把新token覆盖cookie中的token
                CookieUtil.setCookie(request,response,"oldToken",token,60*60*24,true);
            }
        }
        return true;
    }
}