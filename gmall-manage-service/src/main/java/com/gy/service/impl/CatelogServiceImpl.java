package com.gy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gy.api.bean.PmsBaseAttrInfo;
import com.gy.api.bean.PmsBaseCatalog1;
import com.gy.api.bean.PmsBaseCatalog2;
import com.gy.api.bean.PmsBaseCatalog3;
import com.gy.api.service.CatelogService;
import com.gy.mapper.PmsBaseAttrInfoMapper;
import com.gy.mapper.PmsBaseCatalog1Mapper;
import com.gy.mapper.PmsBaseCatalog2Mapper;
import com.gy.mapper.PmsBaseCatalog3Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by gaoyong on 2020/3/23.
 */
@Service
public class CatelogServiceImpl implements CatelogService {

    @Resource
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;
    @Resource
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;
    @Resource
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;
    @Resource
    private PmsBaseAttrInfoMapper baseAttrInfoMapper;
    @Override
    public List<PmsBaseCatalog1> getCatelog1() {
        List<PmsBaseCatalog1> pmsBaseCatalog1s = pmsBaseCatalog1Mapper.selectAll();
        return pmsBaseCatalog1s;
    }

    @Override
    public List<PmsBaseCatalog2> getCatelog2(String catalog1Id) {
        Example example = new Example(PmsBaseCatalog2.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("catalog1Id",catalog1Id);
        List<PmsBaseCatalog2> pmsBaseCatalog2s = pmsBaseCatalog2Mapper.selectByExample(example);
        return pmsBaseCatalog2s;
    }

    @Override
    public List<PmsBaseCatalog3> getCatelog3(String catalog2Id) {
        Example example = new Example(PmsBaseCatalog3.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("catalog2Id",catalog2Id);
        List<PmsBaseCatalog3> pmsBaseCatalog3s = pmsBaseCatalog3Mapper.selectByExample(example);
        return pmsBaseCatalog3s;
    }
}
