package com.gy.mapper;

import com.gy.api.bean.PmsSkuInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by gaoyong on 2020/3/29.
 */
public interface PmsSkuInfoMapper extends Mapper<PmsSkuInfo>{

    /**
     * 根据productId查找skuInfo集合和skuSaleAttrValue集合，组装成hash表返回前端页面
     * @param spuId
     * @return
     */
    List<PmsSkuInfo> selectSkuSaleAttrValueListBySpu(@Param("spuId") String spuId);
}
