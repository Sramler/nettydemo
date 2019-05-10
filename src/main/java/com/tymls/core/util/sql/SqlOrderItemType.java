package com.tymls.core.util.sql;

public enum SqlOrderItemType {
  ASC(1, "升序"),
  DESC(-1, "降序"),
  DEFAULT(0, "默认");

  private int index;
  private String name;

  private SqlOrderItemType(int index, String name) {
    this.index = index;
    this.name = name;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
