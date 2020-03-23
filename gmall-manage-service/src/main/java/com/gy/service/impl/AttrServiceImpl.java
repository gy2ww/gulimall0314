package com.gy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gy.api.bean.PmsBaseAttrInfo;
import com.gy.api.bean.PmsBaseAttrValue;
import com.gy.api.service.AttrService;
import com.gy.mapper.PmsBaseAttrInfoMapper;
import com.gy.mapper.PmsBaseAttrValueMapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by gaoyong on 2020/3/23.
 */
@Service
public class AttrServiceImpl implements AttrService {

    @Resource
    private PmsBaseAttrInfoMapper  pmsBaseAttrInfoMapper;
    @Resource
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;
    @Override
    public List<PmsBaseAttrInfo> getattrInfoList(String catalog3Id) {
        Example example = new Example(PmsBaseAttrInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("catalog3Id",catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectByExample(example);
        return pmsBaseAttrInfos;
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        Example example = new Example(PmsBaseAttrValue.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("attrId",attrId);
        List<PmsBaseAttrValue> pmsBaseAttrValueList = pmsBaseAttrValueMapper.selectByExample(example);
        return pmsBaseAttrValueList;
    }
}
