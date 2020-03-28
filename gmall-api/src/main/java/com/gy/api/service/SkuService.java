package com.gy.api.service;

import com.gy.api.bean.PmsProductImage;
import com.gy.api.bean.PmsProductSaleAttr;

import java.util.List;

/**
 * Created by gaoyong on 2020/3/28.
 */
public interface SkuService {


    /**
     * 获取商品图片列表
     * @param spuId
     * @return
     */
    List<PmsProductImage> spuImageList(String spuId);

    /**
     * 获取商品销售属性
     * @param spuId
     * @return
     */
    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);
}
