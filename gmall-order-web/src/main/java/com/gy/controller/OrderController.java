package com.gy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gy.api.bean.OmsCartItem;
import com.gy.api.bean.OmsOrder;
import com.gy.api.bean.OmsOrderItem;
import com.gy.api.bean.UmsMemberReceiveAddress;
import com.gy.api.service.CartService;
import com.gy.api.service.OrderService;
import com.gy.api.service.SkuService;
import com.gy.api.service.userService;
import com.gy.webutil.annotations.NeedLogin;
import com.gy.webutil.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gaoyong on 2020/4/27.
 */
@Controller
public class OrderController {

    @Reference
    private userService userService;

    @Reference
    private CartService cartService;

    @Reference
    private OrderService orderService;
    @Reference
    private SkuService skuService;

    @RequestMapping("/submitOrder")
    @NeedLogin(LoginSucess = true)
    public ModelAndView submitOrder(HttpServletRequest request, String deliveryAddressId, BigDecimal totalAmount, String tradeCode) {

        List<OmsOrderItem> omsOrderItems = new ArrayList<>();
        String memberId = String.valueOf(request.getAttribute("memberId"));

        //防止用户进行多次提交订单，利用唯一的结算码
        //1.获取缓存中用户结算码进行对比
        String success = orderService.getTradeCode(tradeCode, memberId);
        //进行判断如果验证交易码返回success表示验证通过
        if (success.equals("success")) {
            //根据用户id查询用户购物车中勾选的商品和总价格
            List<OmsCartItem> cartItems = cartService.getCartListByMemberId(memberId);
            OmsOrder omsOrder = new OmsOrder();
            String outTradeNo = "gmall";
            outTradeNo = outTradeNo+System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDHHmmss");
            outTradeNo = outTradeNo+sdf.format(new Date());
            omsOrder.setOrderSn(outTradeNo);
            omsOrder.setCreateTime(new Date());
            omsOrder.setMemberId(memberId);
            omsOrder.setNote("尽快发货");
            omsOrder.setOrderType(1);
            //查询用户收货地址
            UmsMemberReceiveAddress umsMemberReceiveAddress = userService.getReceiverAddressById(deliveryAddressId);
            omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
            omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
            omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
            omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
            omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
            omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
            omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());

            for (int i = 0; i < cartItems.size(); i++) {
                OmsCartItem cartItem = cartItems.get(i);
                //在提交订单之前要进行核对价格和库存
                boolean b = skuService.checkPrice(cartItem.getProductSkuId(),cartItem.getPrice());
                //如果价格验证不通过直接跳转到对应页面
                if (!b) {
                    ModelAndView mv = new ModelAndView("tradeFail");
                    return mv;
                }
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductPic(cartItem.getProductPic());
                omsOrderItem.setProductName(cartItem.getProductName());
                omsOrderItem.setProductQuantity(cartItem.getQuantity());
                omsOrderItem.setProductPrice(cartItem.getPrice());
                //订单号
                String outTradeNo1 = "gmall";
                outTradeNo1 = outTradeNo1+System.currentTimeMillis();
                SimpleDateFormat sdf1 = new SimpleDateFormat("YYYYMMDDHHmmss");
                outTradeNo1 = outTradeNo1+sdf1.format(new Date());
                omsOrderItem.setOrderSn(outTradeNo1);
                omsOrderItem.setProductSkuId(cartItem.getProductSkuId());
                omsOrderItem.setRealAmount(cartItem.getTotalPrice());
                omsOrderItems.add(omsOrderItem);
            }

             omsOrder.setOmsOrderItemList(omsOrderItems);

            //添加订单表信息和删除购物车表的信息
             orderService.saveOrder(omsOrder);
            //重定向到支付系统
            ModelAndView mv = new ModelAndView();
            mv.setViewName("redirect:http://payment.gmall.com:8076/index");
            mv.addObject("outTradeNo",outTradeNo);
            mv.addObject("totalAmount",totalAmount);
            return mv;
        } else {
            ModelAndView mv = new ModelAndView("tradeFail");
            return mv;
        }
    }

    @RequestMapping("/toTrade")
    @NeedLogin(LoginSucess = true)
    public String toTrade(HttpServletRequest request, ModelMap map) {

        String memberId = (String) request.getAttribute("memberId");
        String password = (String) request.getAttribute("password");
        //查询用户的地址信息
        List<UmsMemberReceiveAddress> userAddressList = userService.getReceiveAddressByMemberId(Long.valueOf(memberId));
        //查询用户购物车的数据
        //如果用户在没有登录的状态添加过购物车商品，那么如果登录了应该把cookie中的商品同步到登录后的购物车中
        List<OmsCartItem> orderDetailList = cartService.getCartListCache(memberId);
        String cartToCookie = CookieUtil.getCookieValue(request, "cartToCookie", true);
        if(StringUtils.isNotBlank(cartToCookie)){
            List<OmsCartItem> omsCartItemList = JSON.parseArray(cartToCookie, OmsCartItem.class);
            orderDetailList.addAll(omsCartItemList);
        }
        //所有订单总额
        BigDecimal totalAmount = new BigDecimal("0");
        //每样商品的总价
        for (OmsCartItem omsCartItem : orderDetailList) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
            totalAmount = totalAmount.add(omsCartItem.getTotalPrice());
        }
        String tradeCode = orderService.genTradeCode(memberId);
        map.put("userAddressList", userAddressList);
        map.put("orderDetailList", orderDetailList);
        map.put("totalAmount", totalAmount);
        map.put("tradeCode", tradeCode);
        return "trade";
    }
}
