package com.gy.mapper;

import com.gy.api.bean.PmsProductSaleAttr;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by gaoyong on 2020/3/25.
 */
public interface PmsProductSaleAttrMapper extends Mapper<PmsProductSaleAttr> {

    /**
     * 查询spu的销售属性列表和销售属性值列表
     * @param spuId
     * @param skuId
     * @return
     */
    List<PmsProductSaleAttr> selectSpuSaleAttrListCheckBySku(@Param("spuId") String spuId, @Param("skuId") String skuId);
}
