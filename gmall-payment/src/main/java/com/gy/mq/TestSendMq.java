package com.gy.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

/**
 * Created by gaoyong on 2020/5/13.
 */
public class TestSendMq {


    public static void main(String[] args) {

        //建立连接  61616是activemq的tcp请求的端口号
        ConnectionFactory connect = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
        try {
            Connection connection = connect.createConnection();
            connection.start();
            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            //创建队列
            Queue testqueue = session.createQueue("eat");

           //建立消息的生产者就是发送者
            MessageProducer producer = session.createProducer(testqueue);
            //创建消息
            TextMessage textMessage=new ActiveMQTextMessage();
            textMessage.setText("今天天气真好,我想吃烤串");
            //设置消息持久化，就是当没有消费者时，消息会一直存在队列中，知道消息设置的过期时间到了
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            //是指延时队列，就是过多久在发送
            //textMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,1000*30);
            //发送消息，如果不开启事务，只要执行了send()方法就会发送到队列中
            producer.send(textMessage);
            //提交事务,如果开启了事务，那么必须执行了commit（）方法，消息才会被发送到队列中
            session.commit();
            //关闭连接，因为消息发送者发送完消息之后就可以直接关闭了，不用一直是开启的状态
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
