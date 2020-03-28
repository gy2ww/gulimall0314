package com.gy.api.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

public class PmsProductSaleAttrValue implements Serializable {

    @Id
    @Column
    private String id;
    @Column(name = "product_id")
    private String spuId;
    @Column
    private String saleAttrId;
    @Column
    private String saleAttrValueName;

    @Transient
    private String isChecked;


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


    public String getSaleAttrValueName() {
        return saleAttrValueName;
    }

    public void setSaleAttrValueName(String saleAttrValueName) {
        this.saleAttrValueName = saleAttrValueName;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }
}
