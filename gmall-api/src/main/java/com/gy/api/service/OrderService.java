package com.gy.api.service;

import com.gy.api.bean.OmsOrder;

import java.math.BigDecimal;

/**
 * Created by gaoyong on 2020/4/28.
 */
public interface OrderService {
    /**
     * 生成用户唯一的结算码
     * @param memberId
     * @return
     */
    String genTradeCode(String memberId);

    /**
     * 获取缓存中的用户结算码
     * @param memberId
     */
    String getTradeCode(String tradeCode,String memberId);

    /**
     * 保存订单并删除购物车信息
     * @param omsOrder
     */
    void saveOrder(OmsOrder omsOrder);

    /**
     * 根据订单号查询订单信息
     * @param outTradeNo
     * @return
     */
    OmsOrder getOrderByOutTradeNo(String outTradeNo);

    /**
     * 支付成功后修改订单状态
     * @param orderSn
     */
    void updateOrderStatus(String orderSn);

}
