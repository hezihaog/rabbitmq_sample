package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 路由模式，相比发布订阅模式，多了RoutingKey
 */
public class Producer03Routing {
    /**
     * Email消息的队列
     */
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    /**
     * 短信消息的队列
     */
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    /**
     * 交换机
     */
    private static final String EXCHANGE_ROUTING_INFORM = "exchange_routing_inform=";
    /**
     * 邮件RoutingKet
     */
    public static final String ROUTINGKEY_EMAIL = "routingkey_email";
    /**
     * 短信RoutingKey
     */
    public static final String ROUTINGKEY_SMS = "routingkey_sms";

    public static void main(String[] args) {
        //1.使用连接工厂，创建连接，并和MQ建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置IP地址和端口号
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        //设置用户名和密码
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //2.设置虚拟机，一个mq服务可以设置多个虚拟机，每个虚拟机就相当于一个独立的mq
        connectionFactory.setVirtualHost("/");
        Connection connection = null;
        Channel channel = null;
        try {
            //1）建立新连接
            connection = connectionFactory.newConnection();
            //2）创建会话通道，生产者和mq服务的所有通信都在channel通道中
            channel = connection.createChannel();
            //3）声明队列，如果队列在mq中没有创建，则创建
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
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
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
            //绑定邮件
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_EMAIL);
            //绑定多个
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, "inform");

            //绑定短信
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS);
            //绑定多个
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, "inform");

            /**
             * 参数：String exchange, String routingKey, BasicProperties props, byte[] body
             *
             * 1.exchange：交换机，如果不指定，则使用默认的交换机，注意要设置为空字符串，不能传null
             * 2.routingKey：路由Key，交换机根据路由Key，将消息转发到指定的队列，如果使用默认交换机，routingKey要设置为队列名称
             * 3.props：消息属性
             * 4.body，消息内容
             */
            //发送邮件的消息
            for (int i = 0; i < 5; i++) {
                String message = "路由模式的消息";
                //4）发送消息，将消息发给交换机即可，交换机再转发到队列给消费者处理
                //注意：发消息时，还需要指定RoutingKey
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTINGKEY_EMAIL, null, message.getBytes());
                System.out.println("发送消息达到mq：" + message);
            }
            //发送短信的消息
            for (int i = 0; i < 5; i++) {
                String message = "路由模式的消息";
                //4）发送消息，将消息发给交换机即可，交换机再转发到队列给消费者处理
                //注意：发消息时，还需要指定RoutingKey
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS, null, message.getBytes());
                System.out.println("发送消息达到mq：" + message);
            }

            //发送inform消息，2个消费者都可以接收到（作用相当于发布、订阅模式）
            for (int i = 0; i < 5; i++) {
                String message = "路由模式的消息";
                //4）发送消息，将消息发给交换机即可，交换机再转发到队列给消费者处理
                //注意：发消息时，还需要指定RoutingKey
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, "inform", null, message.getBytes());
                System.out.println("发送消息达到mq：" + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //1.关闭通道
                if (channel != null) {
                    channel.close();
                }
                //2.关闭连接
                if (connection != null) {
                    connection.close();
                }
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }
}