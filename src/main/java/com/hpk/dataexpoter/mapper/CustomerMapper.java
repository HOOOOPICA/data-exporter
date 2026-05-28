package com.hpk.dataexpoter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpk.dataexpoter.model.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}