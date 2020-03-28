package com.gy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gy.api.bean.PmsBaseAttrInfo;
import com.gy.api.bean.PmsBaseAttrValue;
import com.gy.api.service.AttrService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by gaoyong on 2020/3/23.
 */
@RestController
@CrossOrigin
public class AttrController {

    @Reference
    private AttrService attrService;

    @RequestMapping("/attrInfoList")
    public List<PmsBaseAttrInfo> getattrInfoList(String catalog3Id){
        List<PmsBaseAttrInfo> attrInfos=attrService.getattrInfoList(catalog3Id);
        return attrInfos;
    }
    @RequestMapping("/getAttrValueList")
    public List<PmsBaseAttrValue> getAttrValueList(String attrId){
        List<PmsBaseAttrValue> pmsBaseAttrValueList = attrService.getAttrValueList(attrId);
        return pmsBaseAttrValueList;
    }

    @RequestMapping("/saveAttrInfo")
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        String success = attrService.saveAttrInfo(pmsBaseAttrInfo);
        return success;
    }
}
