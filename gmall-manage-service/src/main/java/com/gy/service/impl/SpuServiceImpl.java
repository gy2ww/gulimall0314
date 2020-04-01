package com.gy.service.impl;

        import com.alibaba.dubbo.config.annotation.Service;
        import com.gy.api.bean.*;
        import com.gy.api.service.SpuService;
        import com.gy.mapper.*;
        import tk.mybatis.mapper.entity.Example;

        import javax.annotation.Resource;
        import java.util.List;

/**
 * Created by gaoyong on 2020/3/25.
 */
@Service
public class SpuServiceImpl implements SpuService {

    @Resource
    private PmsProductInfoMapper pmsProductInfoMapper;
    @Resource
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Resource
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;
    @Resource
    private PmsProductImageMapper pmsProductImageMapper;
    @Resource
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        Example example = new Example(PmsProductInfo.class);
        example.createCriteria().andEqualTo("catalog3Id", catalog3Id);
        List<PmsProductInfo> pmsProductInfoList = pmsProductInfoMapper.selectByExample(example);
        return pmsProductInfoList;
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        List<PmsBaseSaleAttr> baseSaleAttrList = pmsBaseSaleAttrMapper.selectAll();
        return baseSaleAttrList;
    }

    @Override
    public String saveSpuInfo(PmsProductInfo pmsProductInfo) {
        int i1 = 0;
        int i = pmsProductInfoMapper.insertSelective(pmsProductInfo);
        if (i > 0) {
            List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
            for (PmsProductImage pmsProductImage : spuImageList) {
                pmsProductImage.setSpuId(pmsProductInfo.getId());
                pmsProductImageMapper.insertSelective(pmsProductImage);
            }
            List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
            for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
                pmsProductSaleAttr.setSpuId(pmsProductInfo.getId());
                int i2 = pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);
                if (i2 > 0) {
                    List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
                    for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                        pmsProductSaleAttrValue.setSpuId(pmsProductInfo.getId());
                        i1 = pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
                    }
                }
            }
        }
        if (i1 > 0) {
            return "success";
        }
        return "failed";
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String spuId,String skuId) {

        //这种方法有瑕疵，就是把数据返回页面的时候没有默认选中状态
  /*      //spu的销售属性集合
        Example example = new Example(PmsProductSaleAttr.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductSaleAttrMapper.selectByExample(example);

        //spu的销售属性值集合
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrList) {
            Example example1 = new Example(PmsProductSaleAttrValue.class);
            example1.createCriteria().andEqualTo("spuId",spuId).andEqualTo("saleAttrId",pmsProductSaleAttr.getSaleAttrId());
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.selectByExample(example1);
            pmsProductSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }
        return pmsProductSaleAttrList;*/
     List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku(spuId,skuId);
     return pmsProductSaleAttrList;
    }
}
