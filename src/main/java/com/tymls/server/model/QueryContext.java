package com.tymls.server.model;

import com.tymls.core.util.sql.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理分布的查询上下文
 *
 * @author luke
 */
public class QueryContext {
  protected int code;
  protected String msg;
  /** 条件 */
  protected WhereList where;

  /** 排序 */
  protected List<SqlOrderByItem> orderBy;

  /** 分页对象 */
  protected Pager pager;

  /** 返回结果 */
  protected List<?> result;

  /** 返回数量 */
  protected Integer total;

  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public WhereList getCondition() {
    return where;
  }

  public void setCondition(WhereList condition) {
    this.where = condition;
  }

  /** 默认分页 */
  public QueryContext() {
    this.pager = new Pager();
  }

  /**
   * * 构造方法，传递分页信息,查询条件和排序条件
   *
   * @param pager 分页对象
   * @param condition 参数map存储
   * @param orderBy 排序list存储
   */
  public QueryContext(Pager pager, WhereList condition, List<SqlOrderByItem> orderBy) {
    this.pager = pager;
    this.where = condition;
    this.orderBy = orderBy;
  }

  /**
   * 推荐使用构造方法，传递分页信息和查询条件
   *
   * @param pager 分页对象
   * @param condition 参数map存储
   */
  public QueryContext(Pager pager, WhereList condition) {
    this.pager = pager;
    this.where = condition;
  }

  public List<SqlOrderByItem> getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(List<SqlOrderByItem> orderBy) {
    this.orderBy = orderBy;
  }

  public QueryContext WHERE(String fieldName, Object equalValue) {
    if (where == null) where = new WhereList();
    // where.add(new WhereFieldItem(fieldName, equalValue));
    where.addWfl(new WhereFieldItem(fieldName, equalValue));
    return this;
  }

  public QueryContext WHERE_EX(String fieldNameEx, Object value) {
    if (where == null) where = new WhereList();
    where.addAll(WhereFieldItem.build(fieldNameEx, value));
    return this;
  }

  public QueryContext WHERE(String fieldName, ConditionOperator op, Object value) {
    if (where == null) where = new WhereList();
    // where.add(new WhereFieldItem(fieldName, op, value));
    where.addWfl(new WhereFieldItem(fieldName, op, value));
    return this;
  }

  public QueryContext FILTER(String filter, Object... args) {
    where.addFilter(filter, args);
    return this;
  }

  public QueryContext ORDER_BY(String fieldName, boolean isAsc) {
    if (orderBy == null) orderBy = new ArrayList<SqlOrderByItem>();
    this.orderBy.add(
        new SqlOrderByItem(fieldName, isAsc ? SqlOrderItemType.ASC : SqlOrderItemType.DESC));
    return this;
  }

  public QueryContext PAGE(String pageIndex, String pageSize) {
    this.pager = new Pager(pageIndex, pageSize);
    return this;
  }

  public QueryContext LIMIT(int start, int limit) {
    this.pager.getPager(start, limit);
    return this;
  }

  public WhereList getWhere() {
    return where;
  }

  public void setWhere(WhereList where) {
    this.where = where;
  }

  public List<?> getResult() {
    return result;
  }

  public void setResult(List<?> result) {
    this.result = result;
  }

  public void setResult(int code, String message, List<?> resultData) {
    this.code = code;
    this.result = resultData;
    this.msg = message;
  }

  public void setPager(int total, int currentPage, int limit) {
    this.pager = new Pager(total, currentPage, limit);
  }
}
