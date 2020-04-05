package com.gy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gy.api.bean.PmsProductSaleAttr;
import com.gy.api.bean.PmsSkuInfo;
import com.gy.api.bean.PmsSkuSaleAttrValue;
import com.gy.api.service.SkuService;

import com.gy.api.service.SpuService;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoyong on 2020/3/30.
 * TODO:如果用springboot的thymelaef模板，那么不能使用@RestController注解，因为会被解析成字符串，而不是模板名称
 */
@Controller
public class ItemController {

    @Reference
    private SkuService skuService;

    @Reference
    private SpuService spuService;


    @RequestMapping("/{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap modelMap, HttpServletRequest request) {

        //获取ip地址
        String ip = request.getRemoteAddr();
        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId,ip);
        modelMap.put("skuInfo", pmsSkuInfo);
        //根据product_id查询spu的销售属性列表
        List<PmsProductSaleAttr> pmsProductSaleAttrList = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getSpuId(), pmsSkuInfo.getId());
        modelMap.put("spuSaleAttrListCheckBySku", pmsProductSaleAttrList);

        //把pms_sku_sale_attr_value的id和pms_sku_Info的id的hash表放到页面上 k:244|246 v:108
        List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getSpuId());
        //hash表
        Map<String, String> map = new HashMap<>();
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            //map的value值
            String skId = skuInfo.getId();
            StringBuffer sb = new StringBuffer();
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                //map的key值，把sku对应的销售属性值的id进行拼接
                String saleAttrValueId = pmsSkuSaleAttrValue.getSaleAttrValueId();
                sb.append(saleAttrValueId).append("|");
            }
            //组装hash表
            map.put(String.valueOf(sb),skId);
        }
        //返回前端的是一个json字符串，而不是一个json对象
        String skuSaleAttrValueListBySpu = JSON.toJSONString(map);
        modelMap.put("skuSaleAttrValueListBySpu",skuSaleAttrValueListBySpu);
        return "item";
    }

    @RequestMapping("/index")
    public String index(ModelMap modelMap) {


        modelMap.put("gaoyong", "是个帅哥！！！");
        return "index";

    }

}
