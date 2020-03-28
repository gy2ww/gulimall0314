package com.gy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gy.api.bean.PmsProductImage;
import com.gy.api.bean.PmsProductSaleAttr;
import com.gy.api.service.SkuService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by gaoyong on 2020/3/28.
 */
@RestController
@CrossOrigin
public class SkuController {

    @Reference
    private SkuService skuService;

    @RequestMapping("/spuImageList")
    public List<PmsProductImage> spuImageList(String spuId){

        List<PmsProductImage> pmsProductImageList = skuService.spuImageList(spuId);
        return pmsProductImageList;
    }
    @RequestMapping("/spuSaleAttrList")
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){
        List<PmsProductSaleAttr> pmsProductSaleAttrList = skuService.spuSaleAttrList(spuId);
        return pmsProductSaleAttrList;
    }
}
