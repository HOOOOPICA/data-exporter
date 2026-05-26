package com.hpk.dataexpoter.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    @ExcelProperty("ID")
    private Long id;

    @ExcelProperty("Order No.")
    private String orderNo;
    @ExcelProperty("Customer Name")
    private String customerName;
    @ExcelProperty("Amount")
    private BigDecimal amount;
    @ExcelProperty("Status")
    private Integer status;
    @ExcelProperty("Created At")
    private LocalDateTime createdAt;
}