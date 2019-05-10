package com.tymls.core.util.sql;

public class SqlOrderByItem {

  public SqlOrderByItem() {
    super();
  }

  public SqlOrderByItem(String field, SqlOrderItemType value) {
    super();
    this.seqNo = 0;
    this.field = field;
    this.value = value;
  }

  public SqlOrderByItem(int seqNo, String field, SqlOrderItemType orderType) {
    super();
    this.seqNo = seqNo;
    this.field = field;
    this.value = orderType;
  }

  private int seqNo = 0;
  private String field;
  private SqlOrderItemType value;

  public String getField() {
    return field;
  }

  public void setField(String fieldName) {
    this.field = fieldName;
  }

  public SqlOrderItemType getValue() {
    return value;
  }

  public void setValue(SqlOrderItemType value) {
    this.value = value;
  }

  public int getSeqNo() {
    return seqNo;
  }
}
