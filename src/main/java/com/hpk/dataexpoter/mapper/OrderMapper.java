package com.hpk.dataexpoter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpk.dataexpoter.model.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    // MyBatis Plus 已经内置了基本的增删改查
}