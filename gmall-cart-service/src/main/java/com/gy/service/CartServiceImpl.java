package com.gy.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.gy.api.bean.OmsCartItem;
import com.gy.api.service.CartService;
import com.gy.mapper.OmsCartItemMapper;
import com.gy.util.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gaoyong on 2020/4/19.
 */
@Service
public class CartServiceImpl implements CartService{



    private final static String KEY="USER:";
    private final static String CART = ":CART";

    @Resource
    OmsCartItemMapper omsCartItemMapper;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public OmsCartItem getCartByMemberId(String memberId, String skuId) {

        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        OmsCartItem omsCartItem1 = omsCartItemMapper.selectByMemberIdAndSkuId(memberId,skuId);
        return omsCartItem1;
    }

    @Override
    public int addNewCart(OmsCartItem omsCartItem) {
        if(StringUtils.isNotBlank(omsCartItem.getMemberId())){
            int i = omsCartItemMapper.insertSelective(omsCartItem);
            return i;
        }
      return 0;
    }

    @Override
    public int updateCart(OmsCartItem omsCartItem1) {

        Example example = new Example(OmsCartItem.class);
        String memberId = omsCartItem1.getMemberId();
        String productSkuId = omsCartItem1.getProductSkuId();
        example.createCriteria().andEqualTo("memberId",omsCartItem1.getMemberId()).andEqualTo("productSkuId",omsCartItem1.getProductSkuId());
        int i = omsCartItemMapper.updateByExampleSelective(omsCartItem1,example);
        return i;
    }

    @Override
    public void syncCache(String memberId) {


        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId",memberId);
        List<OmsCartItem> omsCartItemList = omsCartItemMapper.selectByExample(example);
        for (OmsCartItem omsCartItem : omsCartItemList) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
        }
        Jedis jedis = redisUtils.getJedis();
        HashMap<String, String> map = new HashMap<>();
        for (OmsCartItem omsCartItem : omsCartItemList) {
            map.put(omsCartItem.getProductSkuId(), JSON.toJSONString(omsCartItem));
        }
        //更新缓存之前先删除
        jedis.del(KEY+memberId+CART);
        jedis.hmset(KEY + memberId + CART, map);
        jedis.close();
    }

    @Override
    public List<OmsCartItem> getCartListCache(String memberId) {

        List<OmsCartItem> omsCartItemList = new ArrayList<>();
          Jedis jedis = null;
        try{
            jedis = redisUtils.getJedis();
            //从redis查询购物车数据
            List<String> hvals = jedis.hvals(KEY + memberId + CART);
            if(hvals.size()==0){
                OmsCartItem omsCartItem = new OmsCartItem();
                omsCartItem.setMemberId(memberId);
                omsCartItemList = omsCartItemMapper.select(omsCartItem);
                HashMap<String, String> map = new HashMap<>();
                for (OmsCartItem omsCartItem1 : omsCartItemList) {
                    map.put(omsCartItem1.getProductSkuId(), JSON.toJSONString(omsCartItem1));
                }
                //更新缓存之前先删除
                jedis.del(KEY+memberId+CART);
                jedis.hmset(KEY + memberId + CART, map);
            }else{
                for (String hval : hvals) {
                    OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                    omsCartItemList.add(omsCartItem);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return omsCartItemList;
    }

    @Override
    public void checkCart(OmsCartItem omsCartItem) {

        Example example = new Example(OmsCartItem.class);
        //根据什么条件修改
        example.createCriteria().andEqualTo("memberId",omsCartItem.getMemberId()).andEqualTo("productSkuId",omsCartItem.getProductSkuId());
        omsCartItemMapper.updateByExampleSelective(omsCartItem,example);
        //修改完成后更新缓存
        syncCache(omsCartItem.getMemberId());
    }

    @Override
    public List<OmsCartItem> getCartListByMemberId(String memberId) {

        List<OmsCartItem> omsCartItemList = new ArrayList<>();
        Jedis jedis = redisUtils.getJedis();
        if(jedis != null){
            String s = jedis.get("user:" + memberId + ":ischecked");
            if(StringUtils.isNotBlank(s)){
                //缓存不为空就从缓存中取值
               omsCartItemList = JSON.parseObject(s, new TypeReference<List<OmsCartItem>>(){});
            }else{
                //缓存为空从数据库获取再存入缓存
                OmsCartItem omsCartItem = new OmsCartItem();
                omsCartItem.setMemberId(memberId);
                omsCartItem.setIsChecked("1");
                omsCartItemList = omsCartItemMapper.select(omsCartItem);
                jedis.del("user:" + memberId + ":ischecked");
                jedis.setex("user:" + memberId + ":ischecked",60*60,JSON.toJSONString(omsCartItemList));
            }

        }
        jedis.close();
        return omsCartItemList;
    }

    @Override
    public void delProduct(String MemberId,String productSkuId, BigDecimal productQuantity) {

        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(MemberId);
        omsCartItem.setProductSkuId(productSkuId);
        OmsCartItem omsCartItem1 = omsCartItemMapper.selectOne(omsCartItem);
        if(omsCartItem1.getQuantity().compareTo(productQuantity)==0){
            omsCartItemMapper.deleteByPrimaryKey(omsCartItem1.getId());
        }else{
            //如果用户在支付之前修改了商品数量那么就修改购物车中的商品数量，而不用删除购物车中的数据了
            omsCartItem1.setQuantity(omsCartItem1.getQuantity().subtract(productQuantity));
            Example example = new Example(OmsCartItem.class);
            example.createCriteria().andEqualTo("id",omsCartItem1.getId());
            omsCartItemMapper.updateByExampleSelective(omsCartItem1,example);
        }
    }
}
