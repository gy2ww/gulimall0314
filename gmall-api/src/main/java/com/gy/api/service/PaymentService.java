package com.gy.api.service;

import com.gy.api.bean.PaymentInfo;

import java.util.Map;

/**
 * Created by gaoyong on 2020/5/11.
 */
public interface PaymentService {
    /**
     *  保存支付信息
     * @param paymentInfo
     */
    void savePaymentInfo(PaymentInfo paymentInfo);

    /**
     * 更新支付状态
     * @param paymentInfo
     */
    void updatePaymentInfo(PaymentInfo paymentInfo);

    /**
     * 发送延时队列检查交易状态
     * @param outTradeNo
     */
    void checkPayStatusDelayed(String outTradeNo,int count);

    /**
     * 查询支付宝支付状态查询接口获取支付状态
     * @param outTradeNo
     */
    Map<String, Object> checkAlipayPayStatus(String outTradeNo);

}
