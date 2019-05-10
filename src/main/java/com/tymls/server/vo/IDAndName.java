package com.tymls.server.vo;

import java.io.Serializable;

/**
 * IDAndName
 *
 * @author admin
 */
public class IDAndName implements Serializable {

  private static final long serialVersionUID = 1L;

  /** 关键字 */
  private Integer id;
  /** 名称 */
  private String name;

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public IDAndName() {}

  public IDAndName(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
