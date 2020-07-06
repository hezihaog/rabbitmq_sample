package com.xuecheng.test.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {
    /**
     * Email消息的队列
     */
    public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    /**
     * 短信消息的队列
     */
    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    /**
     * 交换机
     */
    public static final String EXCHANGE_TOPICS_INFORM = "exchange_topics_inform";
    /**
     * 邮件RoutingKet，使用通配符，只接收邮件的用户，发送inform.email。都接收的用户，发送inform.email.sms
     */
    public static final String ROUTINGKEY_EMAIL = "inform.#.email.#";
    /**
     * 短信RoutingKey，使用通配符，只接收短信的用户，发送inform.sms。都接收的用户，发送inform.email.sms
     */
    public static final String ROUTINGKEY_SMS = "inform.#.sms.#";

    //声明交换机
    @Bean("exchange")
    public Exchange exchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM)
                //支持持久化，MQ重启后，交换机还在
                .durable(true)
                .build();
    }

    /**
     * 声明邮件队列
     */
    @Bean("emailQueue")
    public Queue emailQueue() {
        return new Queue(QUEUE_INFORM_EMAIL);
    }

    /**
     * 声明短信队列
     */
    @Bean("smsQueue")
    public Queue smsQueue() {
        return new Queue(QUEUE_INFORM_SMS);
    }

    /**
     * 绑定交换机和邮件队列
     */
    @Bean
    public Binding bindingEmailQueue(@Qualifier("emailQueue") Queue queue,
                                     @Qualifier("exchange") Exchange exchange) {
        return BindingBuilder
                //指定队列
                .bind(queue)
                //指定交换机
                .to(exchange)
                //指定RoutingKey
                .with(ROUTINGKEY_EMAIL)
                .noargs();
    }

    /**
     * 绑定交换机和短信队列
     */
    @Bean
    public Binding bindingSmsQueue(@Qualifier("smsQueue") Queue queue,
                                   @Qualifier("exchange") Exchange exchange) {
        return BindingBuilder
                //指定队列
                .bind(queue)
                //指定交换机
                .to(exchange)
                //指定RoutingKey
                .with(ROUTINGKEY_SMS)
                .noargs();
    }
}