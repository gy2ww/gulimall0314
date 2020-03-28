package com.gy.api.service;

import com.gy.api.bean.PmsBaseSaleAttr;
import com.gy.api.bean.PmsProductInfo;
import com.gy.api.bean.PmsProductSaleAttr;

import java.util.List;

/**
 * Created by gaoyong on 2020/3/25.
 */
public interface SpuService {

    /**
     * 查询spu列表
     * @param catalog3Id
     * @return
     */
    List<PmsProductInfo> spuList(String catalog3Id);

    /**
     * 获取销售属性列表
     * @return
     */
    List<PmsBaseSaleAttr> baseSaleAttrList();

    /**
     * 添加商品spu
     * @param pmsProductInfo
     * @return
     */
    String saveSpuInfo(PmsProductInfo pmsProductInfo);
}
