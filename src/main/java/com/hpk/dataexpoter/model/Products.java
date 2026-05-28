package com.hpk.dataexpoter.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("products")
public class Products {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private String productName;

}
