package com.hpk.dataexpoter.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderExportVO {

    @ExcelProperty("订单号")
    private String orderNo;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("客户等级")
    private String customerLevel;

    @ExcelProperty("商品名称")
    private String productName;

    @ExcelProperty("金额")
    private BigDecimal amount;

    @ExcelProperty("状态")
    private Integer status;

    @ExcelProperty("创建时间")
    private LocalDateTime createdAt;
}