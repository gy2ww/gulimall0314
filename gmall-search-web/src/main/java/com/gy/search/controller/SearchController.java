package com.gy.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gy.api.service.SearchService;
import com.gy.api.bean.PmsSearchParam;
import com.gy.api.bean.PmsSearchSkuInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

/**
 * Created by gaoyong on 2020/4/14.
 */
@Controller
public class SearchController {


    @Reference
    private SearchService searchService;
    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap map){

       List<PmsSearchSkuInfo>  pmsSearchSkuInfoList = searchService.list(pmsSearchParam);
       //把查询结果返回前台 skuLsInfoList是前台页面用的名字
       map.put("skuLsInfoList",pmsSearchSkuInfoList);
       return "list";
    }

    @RequestMapping("/index")
    public String index(){
        return "index";
    }
}
