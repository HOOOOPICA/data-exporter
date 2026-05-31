package com.hpk.dataexpoter.service.mq;

import com.hpk.dataexpoter.config.RabbitMQConfig;
import com.hpk.dataexpoter.service.AsyncExportService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExportMessageConsumer {

    @Autowired
    private AsyncExportService asyncExportService;

    /**
     * 监听队列，收到消息后执行导出任务
     * @param taskId
     */
    @RabbitListener(queues = RabbitMQConfig.EXPORT_QUEUE)
    public void handleExportTask(Long taskId){
        System.out.println("接收到导出任务, taskId: " + taskId);
        asyncExportService.executeTask(taskId);
    }
}
