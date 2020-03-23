package com.gy.api.service;

import com.gy.api.bean.PmsBaseAttrInfo;
import com.gy.api.bean.PmsBaseAttrValue;

import java.util.List;

/**
 * Created by gaoyong on 2020/3/23.
 */
public interface AttrService {
    /**
     * 获取商品属性
     * @param catalog3Id
     * @return
     */
    List<PmsBaseAttrInfo> getattrInfoList(String catalog3Id);

    /**
     * 查询商品属性值
     * @param attrId
     * @return
     */
    List<PmsBaseAttrValue> getAttrValueList(String attrId);
}
