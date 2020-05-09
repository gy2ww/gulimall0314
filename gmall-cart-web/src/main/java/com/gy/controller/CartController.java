package com.gy.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gy.api.bean.OmsCartItem;
import com.gy.api.bean.PmsSkuInfo;
import com.gy.api.service.CartService;
import com.gy.api.service.SkuService;
import com.gy.util.MD5Util;
import com.gy.webutil.annotations.NeedLogin;
import com.gy.webutil.util.CookieUtil;
import com.gy.webutil.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoyong on 2020/4/18.
 */
@Controller
public class CartController {

    @Reference
    SkuService skuService;
    @Reference
    CartService cartService;

    @RequestMapping("/checkCart")
    @NeedLogin(LoginSucess = false)
    public String checkCart(String isChecked,String skuId,ModelMap map){

        String memberId = "1";
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setMemberId(memberId);
        omsCartItem.setIsChecked(isChecked);
        //更新购物车的选中状态
        cartService.checkCart(omsCartItem);
        //从缓存中查询最新的信息
        List<OmsCartItem> cartListCache = cartService.getCartListCache(memberId);
/*
        for (OmsCartItem cartItem : cartListCache) {
            cartItem.setTotalPrice(cartItem.getPrice().multiply(cartItem.getQuantity()));
        }
*/
        BigDecimal totalAmount = getTotalAmount(cartListCache);
        map.put("totalAmount",totalAmount);
        map.put("cartList",cartListCache);
        return "cartListInner";
    }

    /**
     * 计算购物车结算总价
     * @param omsCartItemList
     * @return
     */
    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItemList) {

        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItemList) {
            if(omsCartItem.getIsChecked().equals("1")){
                BigDecimal totalPrice = omsCartItem.getTotalPrice();
                totalAmount=totalAmount.add(totalPrice);
            }
        }
        return totalAmount;
    }


    @RequestMapping("/addToCart")
    @NeedLogin(LoginSucess = false)
    public String addToCart(String skuId, int quantity, HttpServletRequest request, HttpServletResponse response){
        StringBuffer requestURL = request.getRequestURL();
        //根据skuId查询相关的sku信息
        PmsSkuInfo skuInfo = skuService.getSkuById(skuId, "");
        //封装购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setSp1(skuInfo.getSkuSaleAttrValueList().get(0).getSaleAttrValueName());
        omsCartItem.setSp2(skuInfo.getSkuSaleAttrValueList().get(1).getSaleAttrValueName());
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(skuInfo.getPrice());
        omsCartItem.setIsChecked("1");
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductId(skuInfo.getSpuId());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuId(skuInfo.getId());
        omsCartItem.setQuantity(new BigDecimal(quantity));
        omsCartItem.setMemberId(String.valueOf(request.getAttribute("memberId")));
        //TODO:还没有做用户系统，暂时没有memberId，以后加上
        String memberId = String.valueOf(request.getAttribute("memberId"));//Strings.EMPTY;
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        //判断是否登录
        if(StringUtils.isBlank(memberId) || memberId.equals("null")){
            //获取cookie中已有的购物车信息
             String cartToCookie = CookieUtil.getCookieValue(request, "cartToCookie", true);
            if(StringUtils.isBlank(cartToCookie)){
                //如果cookie中购物车信息为空，就直接存入
                omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
                omsCartItems.add(omsCartItem);
            }else{
                //把cookie中原有的购物车信息解析成集合
                omsCartItems = JSON.parseArray(cartToCookie,OmsCartItem.class);
                //判断当前要添加购物车的商品在cookie中是否已经存在
                boolean exist = isExist(omsCartItems, omsCartItem);
                if(exist){
                    //如果cookie中的购物车信息不为空，那么就循环比较要存入购物车的sku信息和cookie中的sku信息，如果哟相同的，就直接把cookie中的sku数量改变
                    for (OmsCartItem cartItem : omsCartItems) {
                        if(cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                            //如果要添加购物车的商品在cookie中有记录，说明用户已经添加过此商品，那么就不需要重复添加，只需把商品数量和总价添加就可以
                            cartItem.setQuantity(cartItem.getQuantity().add(omsCartItem.getQuantity()));
                            //cartItem.setPrice(cartItem.getPrice().add(omsCartItem.getPrice()));
                        }
                    }
                }else{
                    omsCartItems.add(omsCartItem);
                }
            }
            //用户未登录，把购物车的商品存入cookie
            CookieUtil.setCookie(request,response,"cartToCookie",JSON.toJSONString(omsCartItems),60*60*72,true);
        }else{
            //用户已登录,先存入数据库在更新缓存
            //先查询当前用户有没有添加过此件商品
            OmsCartItem omsCartItem1 = cartService.getCartByMemberId(memberId,skuId);
            if(null==omsCartItem1){
                //此用户之前没有添加过此商品
                int i = cartService.addNewCart(omsCartItem);
            }else{
                //此用户之前添加过此商品
                omsCartItem1.setQuantity(omsCartItem1.getQuantity().add(omsCartItem.getQuantity()));
                omsCartItem1.setIsChecked(omsCartItem.getIsChecked());
                //修改商品数量
                cartService.updateCart(omsCartItem1);
            }
            //处理完数据库，更新缓存
            cartService.syncCache(memberId);
        }
        return "redirect:success.html";
    }

    public boolean isExist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {

        for (OmsCartItem cartItem : omsCartItems) {
            if(cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                return true;
            }
        }
        return false;
    }

    @RequestMapping("/cartList")
    @NeedLogin(LoginSucess = false)
    public  String cartList(String token,HttpServletRequest request, ModelMap map){

        //j解析token
        String keyName = MD5Util.md5Encrypt32Lower("gmall0314");
        String ip = MD5Util.md5Encrypt32Lower("127.0.0.1");
        String memberId = Strings.EMPTY;
        if(StringUtils.isNotBlank(token)){
            Map<String, Object> decode = JwtUtil.decode(token, keyName, ip);
            memberId = String.valueOf(decode.get("memberId"));
        }
        List<OmsCartItem> omsCartItemList = new ArrayList<>();
        if(StringUtils.isNotBlank(memberId)){
            //从缓存取数据,用户已登录
            omsCartItemList=cartService.getCartListCache(memberId);
        }else{
            //用户未登录，从cookie中取数据
            String cartToCookie = CookieUtil.getCookieValue(request, "cartToCookie", true);
            if(StringUtils.isNotBlank(cartToCookie)){
                List<OmsCartItem> omsCartItemList1 = JSON.parseArray(cartToCookie, OmsCartItem.class);
                for (OmsCartItem omsCartItem : omsCartItemList1) {
                    omsCartItemList.add(omsCartItem);
                }
            }
        }
        if(omsCartItemList.size()!=0){
            //计算购物车结算总价
            for (OmsCartItem omsCartItem : omsCartItemList) {
                omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
            }
            BigDecimal totalAmount = getTotalAmount(omsCartItemList);
            map.put("totalAmount",totalAmount);
        }
/*
        for (OmsCartItem omsCartItem : omsCartItemList) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
        }
*/
        map.put("cartList",omsCartItemList);
        map.put("token",token);
        return "cartList";

    }
}
