package com.hpk.dataexpoter.controller;

import com.hpk.dataexpoter.common.ExportTaskStatus;
import com.hpk.dataexpoter.model.ExportTask;
import com.hpk.dataexpoter.model.Order;
import com.hpk.dataexpoter.service.AsyncExportService;
import com.hpk.dataexpoter.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class ExportController {
    @Autowired
    private ExportService exportService;

    @Autowired
    private AsyncExportService asyncExportService;

    @GetMapping("/queryAll")
    public List<Order> queryAll() throws InterruptedException {
        return exportService.queryAllOrders();
    }

    @GetMapping("/queryAllParallel")
    public List<Order> queryAllParallel() throws Exception {
        return exportService.queryAllOrdersParallel();
    }

    /**
     * 一次性查出所有数据，再写excel
     * @param response
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     */
    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) throws ExecutionException, InterruptedException, IOException {
        // 告诉浏览器，这是一个Excel文件下载
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String filename = URLEncoder.encode("订单信息", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".xlsx");

        exportService.exportOnce(response.getOutputStream());
    }


   // 边读边写
    @GetMapping("/export/stream/parallel")
    public void exportExcelStreamParallel(HttpServletResponse response) throws IOException, ExecutionException, InterruptedException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode("订单信息_流式", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".xlsx");

        exportService.exportOrdersStreamParallel(response.getOutputStream());
    }

    @GetMapping("/export/stream/serial")
    public void exportExcelStreamSerial(HttpServletResponse response) throws IOException, ExecutionException, InterruptedException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode("订单信息_流式", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".xlsx");

        exportService.exportOrdersStreamSerial(response.getOutputStream());
    }

    @PostMapping("/async")
    public Map<String, Object> submitExportTask() {
        Long taskId = asyncExportService.createTask();
        asyncExportService.executeTask(taskId);  // 异步执行，立刻返回

        Map<String, Object> result = new HashMap<>();
        result.put("taskId", taskId);
        result.put("message", "导出任务已提交，请稍后查询结果");
        return result;
    }

    @GetMapping("/async/{taskId}")
    public ExportTask getTaskStatus(@PathVariable Long taskId) {
        return asyncExportService.getTask(taskId);
    }

    @GetMapping("/async/{taskId}/download")
    public void downloadFile(@PathVariable Long taskId,
                             HttpServletResponse response) throws Exception {
        ExportTask task = asyncExportService.getTask(taskId);

        if (task == null || task.getStatus() != ExportTaskStatus.SUCCESS) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String filename = URLEncoder.encode("订单导出_" + taskId, StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".xlsx");

        // 从磁盘读取文件，写入响应流
        Files.copy(Paths.get(task.getFilePath()), response.getOutputStream());
    }

}
