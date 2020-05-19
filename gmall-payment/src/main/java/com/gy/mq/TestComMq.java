package com.gy.mq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by gaoyong on 2020/5/13.
 */
public class TestComMq {

    public static void main(String[] args) {
        //建立连接
        ConnectionFactory connect = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,ActiveMQConnection.DEFAULT_PASSWORD,"tcp://127.0.0.1:61616");
        try {
            Connection connection = connect.createConnection();
            connection.start();
            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建队列 和消息发送者的队列名要一致，应该是消费者主动去监听mq中的队列
            Destination testqueue = session.createQueue("eat");

            //建立消费者
            MessageConsumer consumer = session.createConsumer(testqueue);
            //监听消息，只要队列中有就消费
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if(message instanceof TextMessage){
                        try {
                            String text = ((TextMessage) message).getText();
                            System.out.println(text+ "   没问题，我请你，LET'S GO!!!");

                            //session.rollback();
                        } catch (JMSException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();;
        }
    }
}
