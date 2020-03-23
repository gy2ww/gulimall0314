package com.gy.api.service;

import com.gy.api.bean.PmsBaseAttrInfo;
import com.gy.api.bean.PmsBaseCatalog1;
import com.gy.api.bean.PmsBaseCatalog2;
import com.gy.api.bean.PmsBaseCatalog3;

import java.util.List;

/**
 * Created by gaoyong on 2020/3/23.
 */
public interface CatelogService {

    /**
     * 查询商品一级分类
     * @return
     */
    List<PmsBaseCatalog1> getCatelog1();

    /**
     * 查询二级分类
     * @param catalog1Id
     * @return
     */
    List<PmsBaseCatalog2> getCatelog2(String catalog1Id);

    /**
     * 查询三级分类
     * @param catalog2Id
     * @return
     */
    List<PmsBaseCatalog3> getCatelog3(String catalog2Id);

}
