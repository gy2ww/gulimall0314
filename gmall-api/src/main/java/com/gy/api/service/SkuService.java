package com.gy.api.service;

import com.gy.api.bean.PmsProductImage;
import com.gy.api.bean.PmsProductSaleAttr;
import com.gy.api.bean.PmsSkuInfo;

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

    /**
     * 添加商品sku相关
     * @param pmsSkuInfo
     * @return
     */
    String saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    /**
     * 商品sku详情
     * @param skuId
     * @return
     */
    PmsSkuInfo getSkuById(String skuId,String ip);

    /**
     * 根据productId查找skuInfo集合和skuSaleAttrValue集合，组装成hash表返回前端页面
     * @param spuId
     * @return
     */
    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String spuId);
}
