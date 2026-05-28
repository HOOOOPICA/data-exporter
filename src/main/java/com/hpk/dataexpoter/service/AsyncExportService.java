package com.hpk.dataexpoter.service;

import com.hpk.dataexpoter.common.ExportTaskStatus;
import com.hpk.dataexpoter.mapper.ExportTaskMapper;
import com.hpk.dataexpoter.model.ExportTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

/**
 * 异步导出文件
 */
@Service
public class AsyncExportService {

    @Autowired
    private ExportTaskMapper exportTaskMapper;

    @Autowired
    private ExportService exportService;

    // 文件存储目录
    private static final String EXPORT_DIR = "./export-files/";

    public Long createTask(){
        ExportTask task = new ExportTask();
        task.setCreatedAt(LocalDateTime.now());
        task.setStatus(ExportTaskStatus.PENDING);
        exportTaskMapper.insert(task);
        return task.getId();
    }

    @Async("exportThreadPool")
    public void executeTask(Long taskId){
        ExportTask exportTask = exportTaskMapper.selectById(taskId);
        exportTask.setStatus(ExportTaskStatus.PROCESSING);
        exportTaskMapper.updateById(exportTask);

        try {
            // 确保目录存在
            Files.createDirectory(Paths.get(EXPORT_DIR));

            // 文件路径
            String filePath = EXPORT_DIR + "orders_" + taskId + ".xlsx";

            //执行流写入
            FileOutputStream fos = new FileOutputStream(filePath);
            exportService.exportOrdersStreamParallel(fos);
            fos.close();

            exportTask.setStatus(ExportTaskStatus.SUCCESS);
            exportTask.setFilePath(filePath);
            exportTask.setFinishedAt(LocalDateTime.now());
            exportTaskMapper.updateById(exportTask);

            // 成功，更新任务状态
        } catch (Exception e) {
            exportTask.setStatus(ExportTaskStatus.SUCCESS);
            exportTask.setErrorMessage(e.getMessage());
            exportTask.setFinishedAt(LocalDateTime.now());
            exportTaskMapper.updateById(exportTask);
        }
    }

    public ExportTask getTask(Long taskId){
        return exportTaskMapper.selectById(taskId);
    }

}
