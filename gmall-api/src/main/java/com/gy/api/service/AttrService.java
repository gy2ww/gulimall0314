package com.gy.api.service;

import com.gy.api.bean.PmsBaseAttrInfo;
import com.gy.api.bean.PmsBaseAttrValue;

import java.util.List;
import java.util.Set;

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

    /**
     * 添加/修改平台属性
     * @param pmsBaseAttrInfo
     * @return
     */
    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    /**
     * 根据attrvalueId查询sku对应的平台属性集合
     * @param set
     * @return
     */
    List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> set);
}
