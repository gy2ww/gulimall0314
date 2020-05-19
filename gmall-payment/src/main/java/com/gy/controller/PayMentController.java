package com.gy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.gy.api.bean.OmsOrder;
import com.gy.api.bean.PaymentInfo;
import com.gy.api.service.OrderService;
import com.gy.api.service.PaymentService;
import com.gy.config.AlipayConfig;
import com.gy.webutil.annotations.NeedLogin;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gaoyong on 2020/5/9.
 */
@Controller
public class PayMentController {


    @Resource
    AlipayClient alipayClient;
    @Reference
    private OrderService orderService;
    @Reference
    private PaymentService paymentService;


    @RequestMapping("/alipay/callback/return")
    @NeedLogin(LoginSucess = true)
    public String alipayCallback(HttpServletRequest request,ModelMap map){

        //获取支付宝返回参数
        String sign = request.getParameter("sign");
        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        request.getParameter("total_amount");
        String callbackcontect = request.getQueryString();
        //验签，因为现在支付宝2.0把同步回调的paramsMap给去掉了，所以同步回调的时候没办法验签，就只能验一下是不是空的
        if(StringUtils.isNotBlank(sign)){
            //更改支付状态
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setCallbackContent(callbackcontect);
            paymentInfo.setAlipayTradeNo(trade_no);
            paymentInfo.setCallbackTime(new Date());
            paymentInfo.setPaymentStatus("已支付");
            paymentInfo.setOrderSn(out_trade_no);
            paymentService.updatePaymentInfo(paymentInfo);
        }
        return "finish";
    }


    @RequestMapping("/alipay/submit")
    @NeedLogin(LoginSucess = true)
    @ResponseBody
    public String alipay(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request,ModelMap map){
       //创建API对应的request
        AlipayTradePagePayRequest alipayRequest =  new  AlipayTradePagePayRequest();
        //在公共参数中设置回跳和通知地址
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
       //封装请求参数
        Map<String, Object> map1 = new HashMap<>();
        map1.put("out_trade_no",outTradeNo);
        map1.put("product_code","FAST_INSTANT_TRADE_PAY");
        map1.put("total_amount",new BigDecimal("0.01"));
        map1.put("subject","超级无敌吊炸天手机");
        String s = JSON.toJSONString(map1);
        alipayRequest.setBizContent(s);
        String form= Strings.EMPTY;
        try  {
            form = alipayClient.pageExecute(alipayRequest).getBody();  //调用SDK生成表单
        }  catch  (AlipayApiException e) {
            e.printStackTrace();
        }
        //根据订单号查询订单信息
        OmsOrder order = orderService.getOrderByOutTradeNo(outTradeNo);

        PaymentInfo paymentInfo = new PaymentInfo();
        //创建时间
        paymentInfo.setCreateTime(new Date());
        //订单表的id
        paymentInfo.setOrderId(order.getId());
        //订单金额
        paymentInfo.setTotalAmount(totalAmount);
        //订单号
        paymentInfo.setOrderSn(outTradeNo);
        paymentInfo.setSubject("超级无敌吊炸天手机");
        //保存支付信息
        paymentService.savePaymentInfo(paymentInfo);

        //发送延时队列检查交易状态
        paymentService.checkPayStatusDelayed(outTradeNo,5);
        System.out.println(form);
        return form;
    }

    @RequestMapping("/index")
    @NeedLogin(LoginSucess = true)
    public String index(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request,ModelMap map){


        map.put("outTradeNo",outTradeNo);
        map.put("totalAmount",totalAmount);
        return "index";
    }
}
