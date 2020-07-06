package com.xuecheng.test.rabbitmq;

import com.xuecheng.test.rabbitmq.config.RabbitMQConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 基于SpringBoot，整合RabbitMQ
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Producer05SpringBoot {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * RabbitTemplate发送消息
     */
    @Test
    public void testSendEmail() {
        String message = "send email msg to user";
        /**
         * 参数
         * 1.交换机名称
         * 2.RoutingKey
         * 3.消息内容
         */
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_TOPICS_INFORM,
                "inform.email",
                message
        );
    }
}