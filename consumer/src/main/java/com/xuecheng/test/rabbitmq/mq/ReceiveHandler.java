package com.xuecheng.test.rabbitmq.mq;

import com.rabbitmq.client.Channel;
import com.xuecheng.test.rabbitmq.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ的消息接收处理类
 */
@Component
public class ReceiveHandler {
    /**
     * 收到邮件消息
     *
     * @param msg     消息字符串
     * @param message 消息对象
     * @param channel 消息通道
     */
    @RabbitListener(queues = {RabbitMQConfig.QUEUE_INFORM_EMAIL})
    public void receiveEmailMsg(String msg, Message message, Channel channel) {
        //得到消息体
        byte[] body = message.getBody();
        System.out.println("接收到邮件消息 => " + msg);
    }

    /**
     * 收到短信消息
     *
     * @param msg     消息字符串
     * @param message 消息对象
     * @param channel 消息通道
     */
    @RabbitListener(queues = {RabbitMQConfig.QUEUE_INFORM_SMS})
    public void receiveSmsMsg(String msg, Message message, Channel channel) {
        //得到消息体
        byte[] body = message.getBody();
        System.out.println("接收到短信消息 => " + msg);
    }
}