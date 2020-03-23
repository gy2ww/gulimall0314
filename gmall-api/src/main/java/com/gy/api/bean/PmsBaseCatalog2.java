package com.gy.api.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

public class PmsBaseCatalog2 implements Serializable {

  @Id
  @Column
  private String id;
  @Column
  private String name;
  @Column
  private String catalog1Id;
  @Transient
  private List<PmsBaseCatalog3> pmsBaseCatalog3List;

  public List<PmsBaseCatalog3> getPmsBaseCatalog3List() {
    return pmsBaseCatalog3List;
  }

  public void setPmsBaseCatalog3List(List<PmsBaseCatalog3> pmsBaseCatalog3List) {
    this.pmsBaseCatalog3List = pmsBaseCatalog3List;
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


  public String getCatalog1Id() {
    return catalog1Id;
  }

  public void setCatalog1Id(String catalog1Id) {
    this.catalog1Id = catalog1Id;
  }

}
