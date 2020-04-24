package com.gy.api.service;

import com.gy.api.bean.OmsCartItem;

import java.util.List;

/**
 * Created by gaoyong on 2020/4/18.
 */
public interface CartService {


    /**
     * 查询此用户之前是否添加过某件商品
     * @param memberId
     * @param skuId
     * @return
     */
    OmsCartItem getCartByMemberId(String memberId, String skuId);

    /**
     * 添加购物车
     * @param omsCartItem
     * @return
     */
    int addNewCart(OmsCartItem omsCartItem);

    /**
     * 修改已添加商品的数量
     * @param omsCartItem1
     * @return
     */
    int updateCart(OmsCartItem omsCartItem1);

    /**
     * 购物车数据库信息同步缓存
     * @param memberId
     */
    void syncCache(String memberId);

    /**
     * 根据memberId查询用户购物车信息
     * @param memberId
     * @return
     */
    List<OmsCartItem> getCartListCache(String memberId);

    /**
     * 修改购物车商品信息
     * @param omsCartItem
     */
    void checkCart(OmsCartItem omsCartItem);
}
