package com.gy.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gy.api.bean.OmsOrder;
import com.gy.api.bean.OmsOrderItem;
import com.gy.api.service.CartService;
import com.gy.api.service.OrderService;
import com.gy.mapper.OmsOrderItemMapper;
import com.gy.mapper.OmsOrderMapper;
import com.gy.mq.SendMq;
import com.gy.util.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * Created by gaoyong on 2020/4/28.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final String KEY = "USER:";
    private static final String TRADE = ":TRADE";
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private OmsOrderMapper omsOrderMapper;
    @Resource
    private OmsOrderItemMapper omsOrderItemMapper;
    @Reference
    private CartService cartService;
    @Resource
    private SendMq sendMq;
    @Override
    public String genTradeCode(String memberId) {

        Jedis jedis = null;
        String tradeCode = UUID.randomUUID().toString();
        try{
            jedis = redisUtils.getJedis();
            String tradeKey = KEY+memberId+TRADE ;
            jedis.del(tradeKey);
            jedis.setex(tradeKey,60*60,tradeCode);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return tradeCode;
    }

    @Override
    public String getTradeCode(String tradeCode,String memberId) {

        Jedis jedis = null;
        try{
            jedis = redisUtils.getJedis();
            String key = KEY+memberId+TRADE;
            if(jedis!=null){
                String tradeCodeCache = jedis.get(key);
                if(StringUtils.isNotBlank(tradeCodeCache) && tradeCodeCache.equals(tradeCode)){
                    //TODO；要加一个lua脚本删除缓存数据，防止在高并发的时候多个用户获取到code值，然后都能验证成功，重复提交
                    jedis.del(key);
                    return "success";
                }else{
                    return "faild";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return null;
    }

    @Override
    public void saveOrder(OmsOrder omsOrder) {

        //保存订单表
        omsOrderMapper.insertSelective(omsOrder);
        String orderId = omsOrder.getId();

        List<OmsOrderItem> omsOrderItemList = omsOrder.getOmsOrderItemList();
        //保存订单详情表信息
        for (OmsOrderItem omsOrderItem : omsOrderItemList) {
            omsOrderItem.setOrderId(orderId);
            int i = omsOrderItemMapper.insertSelective(omsOrderItem);
            //保存一条删除一条
            if(i>0){
               // cartService.delProduct(omsOrder.getMemberId(),omsOrderItem.getProductSkuId(),omsOrderItem.getProductQuantity());
            }
        }
    }

    @Override
    public OmsOrder getOrderByOutTradeNo(String outTradeNo) {

        OmsOrder order = new OmsOrder();
        order.setOrderSn(outTradeNo);
        OmsOrder order1 = omsOrderMapper.selectOne(order);
        return order1;
    }

    @Override
    public void updateOrderStatus(String orderSn) {

        int i = 0;
        OmsOrder omsOrder1 = new OmsOrder();
        omsOrder1.setOrderSn(orderSn);
        OmsOrder omsOrder2 = omsOrderMapper.selectOne(omsOrder1);
        if(!omsOrder2.getStatus().equals("1")){
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setStatus("1");
            Example example = new Example(OmsOrder.class);
            example.createCriteria().andEqualTo("orderSn",orderSn);
            //更新订单状态
             i = omsOrderMapper.updateByExampleSelective(omsOrder, example);

        }
            //更新成功后发送消息到库存系统
            sendMq.ToSendMq("ORDER_PAYMENT",orderSn,0,0);
    }
}
