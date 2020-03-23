package com.gy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gy.api.bean.*;
import com.gy.api.service.CatelogService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by gaoyong on 2020/3/22.
 */
@RestController
@CrossOrigin
public class CatelogController {

    @Reference
    private CatelogService catelogService;


    @RequestMapping("/getCatalog1")
    public List<PmsBaseCatalog1> getCatalog1(){
        List<PmsBaseCatalog1> catelog1=catelogService.getCatelog1();
        return catelog1;
    }
    @RequestMapping("/getCatalog2")
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id){
        List<PmsBaseCatalog2> catelog2=catelogService.getCatelog2(catalog1Id);
        return catelog2;
    }
    @RequestMapping("/getCatalog3")
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id){
        List<PmsBaseCatalog3> catelog3=catelogService.getCatelog3(catalog2Id);
        return catelog3;
    }

}
