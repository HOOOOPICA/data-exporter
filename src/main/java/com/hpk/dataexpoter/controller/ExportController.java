package com.hpk.dataexpoter.controller;

import com.hpk.dataexpoter.model.Order;
import com.hpk.dataexpoter.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
