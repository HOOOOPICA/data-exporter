package com.hpk.dataexpoter.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    // 定义队列、交换机
    public static final String EXPORT_QUEUE = "export.queue";

    public static final String EXPORT_EXCHANGE = "export.exchange";

    public static final String EXPORT_ROUTING_KEY = "export.routingKey";

    // 声明： 队列持久化
    @Bean
    public Queue exportQueue() {
        return new Queue(EXPORT_QUEUE, true);
    }

    // 声明：交换机类型
    @Bean
    public DirectExchange exportExchange() {
        return new DirectExchange(EXPORT_EXCHANGE,true, false);
    }

    // 把队列绑定到交换机
    @Bean
    public Binding exportBinding() {
        return BindingBuilder.
                bind(exportQueue()).
                to(exportExchange()).
                with(EXPORT_ROUTING_KEY);
    }
}
