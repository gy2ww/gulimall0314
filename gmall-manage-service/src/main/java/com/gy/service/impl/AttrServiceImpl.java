package com.gy.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.gy.api.bean.PmsBaseAttrInfo;
import com.gy.api.bean.PmsBaseAttrValue;
import com.gy.api.service.AttrService;
import com.gy.mapper.PmsBaseAttrInfoMapper;
import com.gy.mapper.PmsBaseAttrValueMapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

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
        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfos) {
            String id = pmsBaseAttrInfo.getId();
            List<PmsBaseAttrValue> attrValueList = getAttrValueList(id);
            pmsBaseAttrInfo.setAttrValueList(attrValueList);
        }
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

    @Override
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        String id = pmsBaseAttrInfo.getId();
        int key = 0;
        if (StringUtils.isBlank(id)) {
            int i = pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);

            List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                if (i > 0) {
                    pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                    key = pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
                }
            }
            if (key > 0) {
                return "success";
            }

        } else {
            //修改属性
            Example example = new Example(PmsBaseAttrInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("id",id);
            pmsBaseAttrInfoMapper.updateByExampleSelective(pmsBaseAttrInfo,example);
            //修改属性值
            List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                Example example1 = new Example(PmsBaseAttrValue.class);
                example1.createCriteria().andEqualTo("id",pmsBaseAttrValue.getId());
                key = pmsBaseAttrValueMapper.updateByExampleSelective(pmsBaseAttrValue, example1);
            }
            if(key>0){
                return "success";
            }
        }
        return "failed";
    }

    @Override
    public List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> set) {

        String valueIdStr = StringUtils.join(set, ",");
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = pmsBaseAttrInfoMapper.selectAttrValueListByValueId(valueIdStr);
        return pmsBaseAttrInfoList;
    }

}
