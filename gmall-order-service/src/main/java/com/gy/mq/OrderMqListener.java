package com.gy.mq;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gy.api.service.OrderService;
import org.apache.activemq.command.Message;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;


/**
 * Created by gaoyong on 2020/5/14.
 */
@Component
public class OrderMqListener {

    @Resource
    OrderService orderService;

    @JmsListener(destination = "PAYMENT_SUCCESS_QUEUE", containerFactory = "jmsQueueListener")
      public void updateOrderStatus(Message message){

        String orderSn = null;
        try {
            orderSn = ((TextMessage) message).getText();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        if(StringUtils.isNotBlank(orderSn)){
            //调用订单服务修改订单状态
            orderService.updateOrderStatus(orderSn);
        }

    }
}
