package com.gy.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gy.api.bean.*;
import com.gy.api.service.AttrService;
import com.gy.api.service.SearchService;
import com.gy.webutil.annotations.NeedLogin;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * Created by gaoyong on 2020/4/14.
 */
@Controller
public class SearchController {


    @Reference
    private SearchService searchService;
    @Reference
    private AttrService attrService;

    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap map) {

        List<PmsSearchSkuInfo> pmsSearchSkuInfoList = searchService.list(pmsSearchParam);
        //把查询结果返回前台 skuLsInfoList是前台页面用的名字
        map.put("skuLsInfoList", pmsSearchSkuInfoList);
        //把查询出来的数据去重
        Set<String> set = new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfoList) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                String valueId = pmsSkuAttrValue.getValueId();
                set.add(valueId);
            }
        }
        //查询sku对应的平台属性列表
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = attrService.getAttrValueListByValueId(set);
        //返回给前台
        map.put("attrList", pmsBaseAttrInfoList);
        //从前台传过来的商品属性值数组，然后在所有数据中遍历比较，看有没有和数组中的属性值相同的，有的话就删除属性值对应的属性组
        String[] delValueIds = pmsSearchParam.getValueId();
        //用迭代器删除，以防出现下标越界
        if (null != delValueIds) {
            Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfoList.iterator();
            List<PmsSearchCrumb> pmsSearchCrumbList = new ArrayList<>();
            while (iterator.hasNext()) {
                PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                    String valueId = pmsBaseAttrValue.getId();
                    for (String delValueId : delValueIds) {
                        if (delValueId.equals(valueId)) {
                            PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                            pmsSearchCrumb.setValueId(delValueId);
                            pmsSearchCrumb.setValueName(pmsBaseAttrInfo.getAttrName()+" : "+pmsBaseAttrValue.getValueName());
                            pmsSearchCrumb.setUrlParam(getUrlParamForCrumb(pmsSearchParam, delValueId));
                            pmsSearchCrumbList.add(pmsSearchCrumb);
                            //删除被选中的平台属性值对应的属性组
                            iterator.remove();
                        }
                    }
                }
            }
            map.put("attrValueSelectedList", pmsSearchCrumbList);
        }
        //拼接url 请求路径
        String urlParam = getUrlParam(pmsSearchParam);
        //把拼接好的url返回给页面
        map.put("urlParam", urlParam);

        //面包屑
        String keyword = pmsSearchParam.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            map.put("keyword", keyword);
        }

/*        //点击面包屑的×号
        List<PmsSearchCrumb> pmsSearchCrumbList = new ArrayList<>();
        if (delValueIds != null) {
            for (String delValueId : delValueIds) {
                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                pmsSearchCrumb.setValueId(delValueId);
                pmsSearchCrumb.setValueName(delValueId);
                pmsSearchCrumb.setUrlParam(getUrlParamForCrumb(pmsSearchParam, delValueId));
                pmsSearchCrumbList.add(pmsSearchCrumb);
            }
        }*/
       // map.put("attrValueSelectedList", pmsSearchCrumbList);
        return "list";
    }
    private String getUrlParamForCrumb(PmsSearchParam pmsSearchParam,String valueId) {

        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] valueIds = pmsSearchParam.getValueId();
        String urlParam = Strings.EMPTY;
        if (StringUtils.isNotBlank(catalog3Id)) {
            //urlParam不为空，表示不是第一个拼接的参数，要加上&分隔
            if (StringUtils.isNotBlank(urlParam)) {
                urlParam += "&catalog3Id=" + catalog3Id;
            } else {
                urlParam += "catalog3Id=" + catalog3Id;
            }
        }
        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isNotBlank(urlParam)) {
                urlParam += "&keyword=" + keyword;
            } else {
                urlParam += "keyword=" + keyword;
            }
        }
        if (null != valueIds) {
            for (String pmsSkuAttrValue : valueIds) {
                     if(StringUtils.isNotBlank(valueId)&&!pmsSkuAttrValue.equals(valueId)){
                         urlParam += "&valueId=" + pmsSkuAttrValue;
                     }
                        }
            }
        return urlParam;
    }

    /**
     * 拼接请求的url,在进行商品搜索时，实现面包屑功能
     * String ...valueId 表示这个参数可以不传，有值就传，没值可以不传
     *
     * @param pmsSearchParam
     * @return
     */
    private String getUrlParam(PmsSearchParam pmsSearchParam) {

        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] valueIds = pmsSearchParam.getValueId();
        String urlParam = Strings.EMPTY;
        if (StringUtils.isNotBlank(catalog3Id)) {
            //urlParam不为空，表示不是第一个拼接的参数，要加上&分隔
            if (StringUtils.isNotBlank(urlParam)) {
                urlParam += "&catalog3Id=" + catalog3Id;
            } else {
                urlParam += "catalog3Id=" + catalog3Id;
            }
        }
        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isNotBlank(urlParam)) {
                urlParam += "&keyword=" + keyword;
            } else {
                urlParam += "keyword=" + keyword;
            }
        }
        if (null != valueIds) {
            for (String pmsSkuAttrValue : valueIds) {

                urlParam += "&valueId=" + pmsSkuAttrValue;

            }
        }
        return urlParam;
    }

    @NeedLogin(LoginSucess = false)
    @RequestMapping("/index")
    public String index() {
        return "index";
    }
}
