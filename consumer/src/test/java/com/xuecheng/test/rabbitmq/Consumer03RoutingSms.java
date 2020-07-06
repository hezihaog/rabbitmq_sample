package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 路由模式，短信消费者
 * 相比发布订阅模式，多了RoutingKey
 */
public class Consumer03RoutingSms {
    /**
     * 短信消息的队列
     */
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    /**
     * 交换机
     */
    private static final String EXCHANGE_ROUTING_INFORM = "exchange_routing_inform=";
    /**
     * 短信RoutingKey
     */
    public static final String ROUTINGKEY_SMS = "routingkey_sms";

    public static void main(String[] args) throws IOException, TimeoutException {
        //1.使用连接工厂，创建连接，并和MQ建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置IP地址和端口号
        connectionFactory.setHost("127,0.0.1");
        connectionFactory.setPort(5672);
        //设置用户名和密码
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //2.设置虚拟机，一个mq服务可以设置多个虚拟机，每个虚拟机就相当于一个独立的mq
        connectionFactory.setVirtualHost("/");
        //1）建立新连接
        Connection connection = connectionFactory.newConnection();
        //2）创建会话通道，生产者和mq服务的所有通信都在channel通道中
        Channel channel = connection.createChannel();
        //声明队列，队列名称必须和消息提供方里的一致
        /**
         * 参数：String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
         *
         * 1.queue：队列名称
         * 2.durable：是否持久化，意思是如果持久化，mq重启后，队列还在
         * 3.exclusive：是否独占连接，队列只允许在该链接中访问，如果connection连接关闭后，队列则自动删除了，如果设置为true，可用于零时创建队列
         * 4.autoDelete，自动删除，队列不再使用时，是否自动删除队列，如果将此参数和exclusive参数都设置为true，则可以实现临时队列（队列不用了，就会自动删除）
         * 5.arguments，队列参数，可以设置一个队列的拓展参数，比如设置存活时间
         */
        //重点：申明2个队列
        channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
        //重点：声明1个交换机
        /**
         * 参数：String exchange, String type
         * 1.exchange：交换机的名称
         * 2.type：交换机的类型
         *  fanout：对应的RabbitMQ的工作模式是publish/subscribe
         *  direct：对应路由工作模式
         *  topic：对应通配符工作模式
         *  headers：对应headers工作模式
         */
        channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);
        //让交换机和队列进行绑定
        /**
         * 参数：String queue, String exchange, String routingKey
         * 1.queue：队列名称
         * 2.exchange：交换机名称
         * 3.routingKey：路由Key，它的作用是根据路由Key的值，将消息转发到指定的队列中
         */
        channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS);

        //3）监听队列
        /**
         * 参数：String queue, boolean autoAck,Consumer callback
         *
         * 1.queue，队列名称
         * 2.autoAck，自动回复，当消费者接收到消息后，要告诉mq，消息已接收，设置为true，表示会自动回复，设置为false，就要自己通过编程实现
         * 3.callback，消费方法，当消费方接收到消息时会回调的方法
         */
        //消费回调
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            /**
             * 当接收到消息后，会回调该方法
             * @param consumerTag 消费者标签，用来标识消费者，需要在监听队列时调用的basicConsume()方法中设置
             * @param envelope 信封，通过envelope
             * @param properties 消息属性，在消息发送方发送消息时，调用basicPublish()时传入
             * @param body 消息内容
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                super.handleDelivery(consumerTag, envelope, properties, body);
                //交换机
                String exchange = envelope.getExchange();
                //消息Id，mq在channel中标识消费id
                long deliveryTag = envelope.getDeliveryTag();
                String msg = new String(body, "UTF-8");
                System.out.println("接收到消息 => id：" + deliveryTag + "， body：" + msg);
            }
        };
        channel.basicConsume(QUEUE_INFORM_SMS, false, consumer);
    }
}