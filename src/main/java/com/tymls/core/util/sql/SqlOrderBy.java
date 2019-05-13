package com.tymls.core.util.sql;

import java.util.*;

public class SqlOrderBy {
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 1L;

  private ArrayList<SqlOrderByItem> items = new ArrayList<SqlOrderByItem>();

  public ArrayList<SqlOrderByItem> getItems() {
    return items;
  }

  public boolean add(SqlOrderByItem vo) {
    boolean result = items.add(vo);
    if (vo.getSeqNo() > 0) sortOrderByItem();
    return result;
  }

  public boolean add(int seqNo, String field, boolean isDesc) {
    if (field != null)
      return this.add(
          new SqlOrderByItem(seqNo, field, isDesc ? SqlOrderItemType.DESC : SqlOrderItemType.ASC));
    else return false;
  }

  public boolean addReplace(SqlOrderByItem vo) {

    for (int i = items.size() - 1; i >= 0; i--) {
      if (items.get(i).getSeqNo() == vo.getSeqNo()) items.remove(i);
    }
    boolean result = this.add(vo);
    sortOrderByItem();
    return result;
  }

  /*
   * 安序号排序，在增加的时候必须重新排序
   */
  private void sortOrderByItem() {
    Collections.sort(
        items,
        new Comparator<SqlOrderByItem>() {
          public int compare(SqlOrderByItem o1, SqlOrderByItem o2) {
            return o2.getSeqNo() - o1.getSeqNo();
          }
        });
  }

  public boolean addAll(Collection<? extends SqlOrderByItem> collection) {
    return this.items.addAll(collection);
  }

  public boolean remove(SqlOrderByItem vo) {
    return this.remove(vo);
  }

  public String listToString() {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < items.size(); i++) {
      if (i + 1 == items.size()) {
        result.append(items.get(i).toString());
      } else {
        result.append(items.get(i).toString() + ",");
      }
    }
    return result.toString();
  }

  public String listToString(List<SqlOrderByItem> vos) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < vos.size(); i++) {
      if (i + 1 == vos.size()) {
        result.append(vos.get(i).toString());
      } else {
        result.append(vos.get(i).toString() + ",");
      }
    }
    return result.toString();
  }

  /**
   * 获取单个的orderby
   *
   * @param filed 所要排序的字段
   * @param type 升降序
   * @return
   */
  public static SqlOrderBy getOrderBy(String filed, SqlOrderItemType type) {

    if (filed == null || filed.trim().equals("") || type == null) {
      return null;
    }

    // 设置排序字段
    SqlOrderByItem item = new SqlOrderByItem();

    item.setField(filed.trim());
    item.setValue(type);
    SqlOrderBy orderBy = new SqlOrderBy();

    orderBy.add(item);

    return orderBy;
  }

  /**
   * 测试方法 知识验证数据是否正确
   *
   * @param args
   */
  public static void main(String[] args) {
    SqlOrderByItem vo = new SqlOrderByItem();
    vo.setField("id");
    vo.setValue(SqlOrderItemType.DESC);

    SqlOrderByItem vo1 = new SqlOrderByItem();
    vo1.setField("name");
    vo1.setValue(SqlOrderItemType.ASC);

    SqlOrderByItem vo2 = new SqlOrderByItem();
    vo2.setField("name2");
    vo2.setValue(SqlOrderItemType.DESC);

    SqlOrderByItem vo3 = new SqlOrderByItem();
    vo3.setField("name3");
    vo3.setValue(SqlOrderItemType.ASC);

    SqlOrderByItem vo4 = new SqlOrderByItem();
    vo4.setField("name4");
    vo4.setValue(SqlOrderItemType.DESC);

    SqlOrderBy ps = new SqlOrderBy();
    ps.add(vo);
    ps.add(vo1);
    ps.add(vo2);
    ps.add(vo3);
    ps.add(vo4);

    //		System.out.println(ps.listToString());
    //		System.out.println(ps1.listToString());
    //
    //		ps.remove(vo3);
    //
    //		ps.remove(vo2);
    //
    //		System.out.println(ps.listToString());
    //
    //		ps1.removeAll(ps);
    //
    //		System.out.println(ps1.listToString());
  }
}
