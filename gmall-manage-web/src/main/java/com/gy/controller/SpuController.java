package com.gy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gy.api.bean.PmsBaseSaleAttr;
import com.gy.api.bean.PmsProductInfo;
import com.gy.api.bean.PmsProductSaleAttr;
import com.gy.api.service.SpuService;
import com.gy.util.FastdfsUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by gaoyong on 2020/3/25.
 */
@RestController
@CrossOrigin
public class SpuController {

    @Reference
    private SpuService spuService;
    @Resource
    private FastdfsUtil fastdfsUtil;
    @RequestMapping("/spuList")
    public List<PmsProductInfo> spuList(String catalog3Id){
        List<PmsProductInfo> pmsProductInfoList = spuService.spuList(catalog3Id);
        return pmsProductInfoList;
    }
    @RequestMapping("/baseSaleAttrList")
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        List<PmsBaseSaleAttr> baseSaleAttrList = spuService.baseSaleAttrList();
        return baseSaleAttrList;
    }
    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile){

        String imgUrl = fastdfsUtil.fileUpload(multipartFile);
        return imgUrl;
    }
    @RequestMapping("/saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        String success = spuService.saveSpuInfo(pmsProductInfo);
        return success;
    }
}
