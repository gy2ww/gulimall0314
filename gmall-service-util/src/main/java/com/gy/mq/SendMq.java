package com.gy.mq;

import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.jms.*;

/**
 * Created by gaoyong on 2020/5/14.
 */
@Configuration
public class SendMq {

    @Resource
    ActiveMQUtil activeMQUtil;


    public void ToSendMq(String queueName, String param,int status,int count){
        Connection connection = null;
        try {
            //建立连接
            connection = activeMQUtil.getConnectionFactory().createConnection();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        Session session = null;
        try {
            //创建session
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            //创建队列
            Queue payment_success_queue = session.createQueue(queueName);
            //创建消息的生产者
            MessageProducer producer = session.createProducer(payment_success_queue);
            //创建消息 把订单号发送过去
            //创建消息
            TextMessage textMessage=new ActiveMQTextMessage();
            textMessage.setText(param);
            //消息持久化
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            //如果状态为1就设置延时队列
            if(status==1){
                textMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,1000*20);
                textMessage.setText(param+" "+count);
            }
            //发送消息
            producer.send(textMessage);
            //提交事务，消息发送到消息队列中
            session.commit();

        } catch (JMSException e) {
            try {
                //发生异常消息回滚
                session.rollback();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        }finally {
            try {
                //关闭连接
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

    }
}
