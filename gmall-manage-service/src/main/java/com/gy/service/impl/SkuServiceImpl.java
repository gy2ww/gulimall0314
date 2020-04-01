package com.gy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gy.api.bean.*;
import com.gy.api.service.SkuService;
import com.gy.mapper.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaoyong on 2020/3/28.
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Resource
    private PmsProductImageMapper pmsProductImageMapper;
    @Resource
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Resource
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;
    @Resource
    private PmsSkuInfoMapper pmsSkuInfoMapper;
    @Resource
    private PmsSkuImageMapper pmsSkuImageMapper;
    @Resource
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Resource
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Override
    public List<PmsProductImage> spuImageList(String spuId) {

        Example example = new Example(PmsProductImage.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<PmsProductImage> pmsProductImageList = pmsProductImageMapper.selectByExample(example);
        return pmsProductImageList;
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {

        Example example = new Example(PmsProductSaleAttr.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductSaleAttrMapper.selectByExample(example);
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrList) {
            Example example1 = new Example(PmsProductSaleAttrValue.class);
            example1.createCriteria().andEqualTo("spuId",pmsProductSaleAttr.getSpuId()).andEqualTo("saleAttrId",pmsProductSaleAttr.getSaleAttrId());
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues1 = pmsProductSaleAttrValueMapper.selectByExample(example1);
            pmsProductSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues1);
        }

        return pmsProductSaleAttrList;
    }

    @Override
    public String saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

        int i=0;
        //添加sku主表
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        //添加skuImg
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
        //添加sku平台属性
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }
        //添加sku销售属性
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(pmsSkuInfo.getId());
            i = pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }
        if(i>0){
            return "success";
        }
        return "failed";
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId) {
        //sku主表
        Example example = new Example(PmsSkuInfo.class);
        example.createCriteria().andEqualTo("id",skuId);
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoMapper.selectOneByExample(example);
        //skuImage
        Example example1 = new Example(PmsSkuImage.class);
        example1.createCriteria().andEqualTo("skuId",skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.selectByExample(example1);
        pmsSkuInfo.setSkuImageList(pmsSkuImages);
        //skuattrvalue平台属性
        PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
        pmsSkuAttrValue.setSkuId(skuId);
        List<PmsSkuAttrValue> pmsSkuAttrValueList = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
        pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValueList);
        //skusaleattrvalue平台销售属性
        PmsSkuSaleAttrValue pmsSkuSaleAttrValue = new PmsSkuSaleAttrValue();
        pmsSkuSaleAttrValue.setSkuId(skuId);
        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList = pmsSkuSaleAttrValueMapper.select(pmsSkuSaleAttrValue);
        pmsSkuInfo.setSkuSaleAttrValueList(pmsSkuSaleAttrValueList);
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String spuId) {

       List<PmsSkuInfo> pmsSkuInfoList =  pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(spuId);
        return pmsSkuInfoList;
    }
}
