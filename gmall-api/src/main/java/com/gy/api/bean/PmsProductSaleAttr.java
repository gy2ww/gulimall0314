package com.gy.api.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author gaoyong
 */
public class PmsProductSaleAttr implements Serializable {

  @Id
  @Column
  private String id;
  @Column(name="product_id")
  private String spuId;
  @Column
  private String saleAttrId;
  @Column
  private String saleAttrName;

  @Transient
  private List<PmsProductSaleAttrValue> spuSaleAttrValueList;

  @Transient
  private Map<String, String> spuSaleAttrValueJson;

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


  public String getSaleAttrId() {
    return saleAttrId;
  }

  public void setSaleAttrId(String saleAttrId) {
    this.saleAttrId = saleAttrId;
  }


  public String getSaleAttrName() {
    return saleAttrName;
  }

  public List<PmsProductSaleAttrValue> getSpuSaleAttrValueList() {
    return spuSaleAttrValueList;
  }

  public void setSpuSaleAttrValueList(List<PmsProductSaleAttrValue> spuSaleAttrValueList) {
    this.spuSaleAttrValueList = spuSaleAttrValueList;
  }

  public Map<String, String> getSpuSaleAttrValueJson() {
    return spuSaleAttrValueJson;
  }

  public void setSpuSaleAttrValueJson(Map<String, String> spuSaleAttrValueJson) {
    this.spuSaleAttrValueJson = spuSaleAttrValueJson;
  }

  public void setSaleAttrName(String saleAttrName) {

    this.saleAttrName = saleAttrName;
  }

}
