package com.hpk.dataexpoter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpk.dataexpoter.mapper.OrderMapper;
import com.hpk.dataexpoter.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class ExportService {

    @Autowired
    private OrderMapper orderMapper;

    // 创建线程池
    private final ExecutorService threadPool = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,     // 核心线程数
            Runtime.getRuntime().availableProcessors() * 2,         // 最大线程数
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100)                   // 等待队列
    );

    /**
     * 查询所有数据（单线程分批查询）
     * 单线程版本(
     */
    public List<Order> queryAllOrders() throws InterruptedException {
        int pageSize = 1000;  // 每批查1000条
        int pageNum = 1;
        List<Order> result = new ArrayList<>();

        while (true) {
            // 模拟慢查询
            Thread.sleep(100);

            // MyBatis Plus 分页查询
            Page<Order> page = orderMapper.selectPage(
                new Page<>(pageNum, pageSize),
                null
            );

            result.addAll(page.getRecords());

            // 没有下一页了，退出
            if (!page.hasNext()) break;

            pageNum++;
        }

        return result;
    }

    public List<Order> queryAllOrdersParallel() throws ExecutionException, InterruptedException {
        // 定义： 一个任务，每次查几条、一共查几条
        // 用 futures 存储任务
        // 等所有任务完成，返回结果
        int pageSize = 1000;
        int totalCount = 50000;
        int totalPages = totalCount / pageSize;

        // 每个线程负责查一批，提交给线程池
        List<Future<List<Order>>> futures = new ArrayList<>();
        for(int i = 0; i <= totalPages; i++){
            final int pageNum = i;
            Future<List<Order>> future = threadPool.submit(() -> {
                // 模拟慢查询
                Thread.sleep(100);

                // 这个任务做什么事: 分批查数据库
                Page<Order> page = orderMapper.selectPage(
                        new Page<>(pageNum, pageSize),
                        null
                );
                return page.getRecords();
            });

            futures.add(future);
        }

        // 等待所有任务完成
        List<Order> result = new ArrayList<>();
        for (Future<List<Order>> future : futures) {
            result.addAll(future.get());
        }
        return result;
    }
}