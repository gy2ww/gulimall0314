package com.gy.api.bean;

import javax.persistence.Column;
import java.io.Serializable;

public class PmsSkuImage implements Serializable {

  private String id;
  private String skuId;
  private String imgName;
  private String imgUrl;
  @Column(name = "product_img_id")
  private String spuImgId;
  private String isDefault = "0";


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getSkuId() {
    return skuId;
  }

  public void setSkuId(String skuId) {
    this.skuId = skuId;
  }


  public String getImgName() {
    return imgName;
  }

  public void setImgName(String imgName) {
    this.imgName = imgName;
  }


  public String getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }


  public String getSpuImgId() {
    return spuImgId;
  }

  public void setSpuImgId(String spuImgId) {
    this.spuImgId = spuImgId;
  }


  public String getIsDefault() {
    return isDefault;
  }

  public void setIsDefault(String isDefault) {
    this.isDefault = isDefault;
  }

}
