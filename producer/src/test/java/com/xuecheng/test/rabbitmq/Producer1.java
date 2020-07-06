package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ的消息生产方，Work Queues模式，一个生产者对应多个消费者，采用轮训的方式，不允许一个消息，被多个消费者消费
 * <p>
 * 应用场景：当有一个计算任务非常消耗CPU资源，需要多个消费者来干活时，使用该模式最好
 */
public class Producer1 {
    /**
     * 队列名称
     */
    private static final String QUEUE_NAME = "mq_hello_world";

    /**
     * 消息生产方，发送步骤
     * <p>
     * 1）创建连接
     * 2）创建通道
     * 3）声明队列
     * 4）发送消息
     */
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
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            /**
             * 参数：String exchange, String routingKey, BasicProperties props, byte[] body
             *
             * 1.exchange：交换机，如果不指定，则使用默认的交换机，注意要设置为空字符串，不能传null
             * 2.routingKey：路由Key，交换机根据路由Key，将消息转发到指定的队列，如果使用默认交换机，routingKey要设置为队列名称
             * 3.props：消息属性
             * 4.body，消息内容
             */
            String message = "hello world rabbit mq";
            //4）发送消息
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("发送消息达到mq：" + message);
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