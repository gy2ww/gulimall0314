package com.gy.service.impl;


import com.gy.api.bean.WareInfo;
import com.gy.api.bean.WareOrderTask;
import com.gy.api.bean.WareSku;
import com.gy.mq.ActiveMQUtil;
import com.gy.mapper.WareInfoMapper;
import com.gy.mapper.WareOrderTaskDetailMapper;
import com.gy.mapper.WareOrderTaskMapper;
import com.gy.mapper.WareSkuMapper;
import com.gy.service.GwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service
public class GwareServiceImpl implements GwareService {

    @Autowired
    private WareSkuMapper wareSkuMapper;

    @Autowired
    private WareInfoMapper wareInfoMapper;

    @Autowired
    private WareOrderTaskMapper wareOrderTaskMapper;

    @Autowired
    private WareOrderTaskDetailMapper wareOrderTaskDetailMapper;

    @Resource
    ActiveMQUtil activeMQUtil;


    @Override
    public Integer getStockBySkuId(String skuid) {
        return null;
    }

    @Override
    public boolean hasStockBySkuId(String skuid, Integer num) {
        return false;
    }

    @Override
    public List<WareInfo> getWareInfoBySkuid(String skuid) {
        return null;
    }

    @Override
    public void addWareInfo() {

    }

    @Override
    public Map<String, List<String>> getWareSkuMap(List<String> skuIdlist) {
        return null;
    }

    @Override
    public void addWareSku(WareSku wareSku) {

    }

    @Override
    public void deliveryStock(WareOrderTask taskExample) {

    }

    @Override
    public WareOrderTask saveWareOrderTask(WareOrderTask wareOrderTask) {
        return null;
    }

    @Override
    public List<WareOrderTask> checkOrderSplit(WareOrderTask wareOrderTask) {
        return null;
    }

    @Override
    public void lockStock(WareOrderTask wareOrderTask) {

    }

    @Override
    public List<WareOrderTask> getWareOrderTaskList(WareOrderTask wareOrderTask) {
        return null;
    }

    @Override
    public List<WareSku> getWareSkuList() {
        return null;
    }

    @Override
    public List<WareInfo> getWareInfoList() {
        return null;
    }
}
