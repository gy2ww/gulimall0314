package com.gy.mq;

import com.gy.api.bean.PaymentInfo;
import com.gy.api.service.PaymentService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.activemq.command.Message;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.Map;

/**
 * Created by gaoyong on 2020/5/15.
 */
@Component
public class PaymentMqListener {

    @Resource
    PaymentService paymentService;


    @JmsListener(destination = "PAYMENT_CHECK_QUEUE",containerFactory = "jmsQueueListener")
    public void paymentDelayedListener(Message message){
        try {
            String mq = ((TextMessage) message).getText();
            String[] split = mq.split(" ");
            String outTradeNo = split[0];
            int count = Integer.parseInt(split[1]);
            //查询支付宝查询接口获取交易状态
            Map<String,Object> resultMap = paymentService.checkAlipayPayStatus(outTradeNo);
            String tradeStatus = String.valueOf(resultMap.get("tradeStatus"));
            if(resultMap==null || resultMap.isEmpty()){
                //重新发送延时队列查询交易状态
                if(count>1){
                    count--;
                    System.out.println("检查次数还剩"+count+"次38");
                    paymentService.checkPayStatusDelayed(outTradeNo,count);

                }else{
                    System.out.println("检查次数用完，结束检查");
                    return;
                }

            }else{
                if(StringUtils.isNotBlank(tradeStatus)){
                    if( !tradeStatus.equals("TRADE_SUCCESS")){
                        System.out.println("支付状态为未支付");
                        //重新发送延时队列，查询支付状态
                        if(count>1){
                            count--;
                            System.out.println("检查次数还剩"+count+"次53");
                            paymentService.checkPayStatusDelayed(outTradeNo,count);
                        }else{
                            System.out.println("检查次数用完，结束检查");
                            return;
                        }
                    }else{
                        System.out.println("支付成功状态为已支付，检查数据库中的状态是否已经更新");
                        PaymentInfo paymentInfo = new PaymentInfo();
                        paymentInfo.setOrderSn(outTradeNo);
                        paymentService.updatePaymentInfo(paymentInfo);
                    }
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
