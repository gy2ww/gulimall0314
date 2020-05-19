package com.gy.mq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * Created by gaoyong on 2020/5/17.
 */
@Component
public class WareMqListener {

    @JmsListener(destination = "ORDER_PAYMENT",containerFactory = "jmsQueueListener")
    public void wareMqListener(TextMessage message)  {

        try {
            String text = message.getText();
            System.out.println(text);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
