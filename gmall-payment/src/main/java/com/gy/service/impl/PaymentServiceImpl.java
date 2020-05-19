package com.gy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.gy.api.bean.PaymentInfo;
import com.gy.api.service.PaymentService;
import com.gy.mapper.PaymentInfoMapper;
import com.gy.mq.ActiveMQUtil;
import com.gy.mq.SendMq;
import org.apache.commons.lang3.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gaoyong on 2020/5/11.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private PaymentInfoMapper paymentInfoMapper;
    @Resource
    private ActiveMQUtil activeMQUtil;
    @Resource
    SendMq sendMq;
    @Resource
    AlipayClient alipayClient;

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {

        int i = paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePaymentInfo(PaymentInfo paymentInfo) {

        PaymentInfo paymentInfo1 = paymentInfoMapper.selectOne(paymentInfo);
        if(paymentInfo1!=null){
            if(StringUtils.isNotBlank(paymentInfo1.getPaymentStatus()) && paymentInfo1.getPaymentStatus().equals("已支付")){
                System.out.println("数据库中的支付状态已更新，不能重复更新");
                return;
            }
        }
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("orderSn", paymentInfo.getOrderSn());
        //更新支付状态
        int i = paymentInfoMapper.updateByExampleSelective(paymentInfo, example);
        //更新状态成功后在发送消息
        if(i>0){
            String queueName = "PAYMENT_SUCCESS_QUEUE";
            String param = paymentInfo.getOrderSn();
            sendMq.ToSendMq(queueName,param,0,0);
        }
    }

    @Override
    public void checkPayStatusDelayed(String outTradeNo,int count) {

        String queueName  = "PAYMENT_CHECK_QUEUE";
        if(count>=1){
            sendMq.ToSendMq(queueName,outTradeNo,1,count);
        }

    }

    @Override
    public Map<String, Object> checkAlipayPayStatus(String outTradeNo) {

        Map<String, Object> resultMap = new HashMap<>();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("out_trade_no",outTradeNo);
        request.setBizContent(JSON.toJSONString(map));
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
            resultMap.put("tradeStatus",response.getTradeStatus());
            System.out.println("查询支付宝的交易状态为："+response.getTradeStatus());

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return resultMap;
    }
}
