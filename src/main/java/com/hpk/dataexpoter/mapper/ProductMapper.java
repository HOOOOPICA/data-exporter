package com.hpk.dataexpoter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpk.dataexpoter.model.Products;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Products> {
}
