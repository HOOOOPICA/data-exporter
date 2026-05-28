package com.hpk.dataexpoter.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("customers")
public class Customer {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String customerName;

    private String level;
}
