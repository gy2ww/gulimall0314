package com.gy.api.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

public class PmsBaseCatalog1 implements Serializable {

  @Id
  @Column
  private String id;
  @Column
  private String name;
  @Transient
  private List<PmsBaseCatalog2> pmsBaseCatalog2List;

  public List<PmsBaseCatalog2> getPmsBaseCatalog2List() {
    return pmsBaseCatalog2List;
  }

  public void setPmsBaseCatalog2List(List<PmsBaseCatalog2> pmsBaseCatalog2List) {
    this.pmsBaseCatalog2List = pmsBaseCatalog2List;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
