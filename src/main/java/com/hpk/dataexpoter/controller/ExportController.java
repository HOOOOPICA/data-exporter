package com.hpk.dataexpoter.controller;

import com.alibaba.excel.EasyExcel;
import com.hpk.dataexpoter.model.Order;
import com.hpk.dataexpoter.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class ExportController {
    @Autowired
    private ExportService exportService;

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

        exportService.exportOrdersStream(response.getOutputStream());
    }
}
