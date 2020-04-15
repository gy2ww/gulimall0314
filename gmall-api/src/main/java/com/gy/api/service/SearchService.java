package com.gy.api.service;

import com.gy.api.bean.PmsSearchParam;
import com.gy.api.bean.PmsSearchSkuInfo;

import java.util.List;

/**
 * Created by gaoyong on 2020/4/14.
 */
public interface SearchService {

    /**
     * 根据搜索条件搜索相关sku列表
     * @param pmsSearchParam
     * @return
     */
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
