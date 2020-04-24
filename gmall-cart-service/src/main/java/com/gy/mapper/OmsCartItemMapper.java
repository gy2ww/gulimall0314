package com.gy.mapper;

import com.gy.api.bean.OmsCartItem;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * Created by gaoyong on 2020/4/19.
 */
public interface OmsCartItemMapper extends Mapper<OmsCartItem>{

    /**
     * 根据用户id和skuid查询用户有没有添加过此商品
     * @param memberId
     * @param skuId
     * @return
     */
    OmsCartItem selectByMemberIdAndSkuId(@Param("memberId") String memberId, @Param("skuId") String skuId);
}
