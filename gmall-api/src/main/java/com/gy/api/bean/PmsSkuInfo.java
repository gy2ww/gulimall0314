package com.gy.api.bean;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class PmsSkuInfo implements Serializable {

  @Id
  @GeneratedValue(generator = "JDBC")//增加这个注解解决了通用Mapper的insert不返回主键的问题
  private String id;
  @Column(name = "product_id")
  private String spuId;
  private BigDecimal price;
  private String skuName;
  private String skuDesc;
  private String weight;
  private String tmId;
  private String catalog3Id;
  private String skuDefaultImg;

  @Transient
  List<PmsSkuImage> skuImageList;
  @Transient
  List<PmsSkuAttrValue> skuAttrValueList;
  @Transient
  List<PmsSkuSaleAttrValue> skuSaleAttrValueList;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getSpuId() {
    return spuId;
  }

  public void setSpuId(String spuId) {
    this.spuId = spuId;
  }


  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }


  public String getSkuName() {
    return skuName;
  }

  public void setSkuName(String skuName) {
    this.skuName = skuName;
  }


  public String getSkuDesc() {
    return skuDesc;
  }

  public void setSkuDesc(String skuDesc) {
    this.skuDesc = skuDesc;
  }


  public String getWeight() {
    return weight;
  }

  public void setWeight(String weight) {
    this.weight = weight;
  }


  public String getTmId() {
    return tmId;
  }

  public void setTmId(String tmId) {
    this.tmId = tmId;
  }


  public String getCatalog3Id() {
    return catalog3Id;
  }

  public void setCatalog3Id(String catalog3Id) {
    this.catalog3Id = catalog3Id;
  }


  public String getSkuDefaultImg() {
    return skuDefaultImg;
  }

  public void setSkuDefaultImg(String skuDefaultImg) {

    this.skuDefaultImg = skuDefaultImg;
  }

  public List<PmsSkuImage> getSkuImageList() {
    return skuImageList;
  }

  public void setSkuImageList(List<PmsSkuImage> skuImageList) {
    this.skuImageList = skuImageList;
  }

  public List<PmsSkuAttrValue> getSkuAttrValueList() {
    return skuAttrValueList;
  }

  public void setSkuAttrValueList(List<PmsSkuAttrValue> skuAttrValueList) {
    this.skuAttrValueList = skuAttrValueList;
  }

  public List<PmsSkuSaleAttrValue> getSkuSaleAttrValueList() {
    return skuSaleAttrValueList;
  }

  public void setSkuSaleAttrValueList(List<PmsSkuSaleAttrValue> skuSaleAttrValueList) {
    this.skuSaleAttrValueList = skuSaleAttrValueList;
  }
}
