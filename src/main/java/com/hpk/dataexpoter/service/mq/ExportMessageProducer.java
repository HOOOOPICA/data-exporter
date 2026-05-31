package com.hpk.dataexpoter.service.mq;

import com.hpk.dataexpoter.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExportMessageProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;



    /**
     * 发送导出任务
     * @param taskId
     */
    public void sendExportTask(Long taskId){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXPORT_EXCHANGE,     // 第一个参数：交换机
                RabbitMQConfig.EXPORT_ROUTING_KEY,  // 第二个参数：routing key
                taskId
        );
        System.out.println("导出任务已发送到队列，taskId：" + taskId);
    }
}
