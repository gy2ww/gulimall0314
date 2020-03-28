package com.gy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gy.api.bean.PmsProductImage;
import com.gy.api.bean.PmsProductSaleAttr;
import com.gy.api.service.SkuService;
import com.gy.mapper.PmsProductImageMapper;
import com.gy.mapper.PmsProductSaleAttrMapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
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
        return pmsProductSaleAttrList;
    }
}
