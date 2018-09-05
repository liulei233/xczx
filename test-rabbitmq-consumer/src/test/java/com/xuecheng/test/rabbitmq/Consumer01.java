package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 测试rabbitmq 入门程序
 *
 * @author Administrator
 * @version 1.0
 * @create 2018-06-29 9:22
 **/
public class Consumer01 {

    private static final String QUEUE = "helloworld";

    public static void main(String[] args) {
        //连接
        Connection connection = null;
        //通道
        Channel channel = null;
        try {
            //给MQ发送消息
            //连接MQ
            //通过连接工厂创建连接
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("127.0.0.1");//IP地址
            connectionFactory.setPort(5672);//默认mq服务端口
            connectionFactory.setUsername("guest");
            connectionFactory.setPassword("guest");
            connectionFactory.setVirtualHost("/");//设置mq虚拟机
            //和MQ创建连接
            connection = connectionFactory.newConnection();
            //建立channel通道，会话通道，在通道中向mq发送消息
            channel = connection.createChannel();

            //声明一个队列,根据队列名称判断，如果在mq中没有此队列就创建一个队列，如果有此队列则不作处理
            /**
             * 参数：String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
             * 1、队列名称
             * 2、是否持久化，true表示持久化，当mq重启之后此队列还在
             * 3、是否独占通道，true表示此通道只能由此队列来使用
             * 4、自动删除，true表示自动删除，mq重启后队列删除
             * 5、队列参数列表
             */
            channel.queueDeclare(QUEUE,true,false,false,null);

            //创建回调方法类
            DefaultConsumer consumer = new DefaultConsumer(channel){

                /**
                 * 消费者接收到消息后会调用此方法
                 * @param consumerTag 消费者标签，用来标识消费，如果不指定默认一个名称
                 * @param envelope 消息内容包
                 * @param properties  消息的属性
                 * @param body 消息的内容
                 * @throws IOException
                 */
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    String exchange = envelope.getExchange();//交换
                    long deliveryTag = envelope.getDeliveryTag();//消息id
                    String routingKey  = envelope.getRoutingKey();//路由key
                    String message = new String(body, "utf-8");
                    System.out.println(message);

                }
            };

            //监听队列,接收消息
            /**
             * 参数：String queue, boolean autoAck, Consumer callback
             * 1、监听队列的名称
             * 2、autoAck是否自动回复，消息者接收到消息要给mq回复表示此消息已接收，此时mq去删除消息
             *    如果autoAck设置true，表示消费者接收到消息后自动回复，如果设置为false，需要程序员在代码中手动回复(channel.basicAck();)
             * 3、回调方法，接收到消息后调用此callback
             */
            channel.basicConsume(QUEUE,true,consumer);



        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


    }
}
