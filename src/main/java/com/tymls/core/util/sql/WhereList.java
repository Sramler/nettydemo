package com.tymls.core.util.sql;

import com.tymls.core.date.DateTimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 查询条件组合
 *
 * @author admin
 */
public class WhereList extends ArrayList<WhereFieldItem> {
  private static final long serialVersionUID = 1L;
  private List<WhereFilterItem> filters = new ArrayList<WhereFilterItem>();
  /** 吴佳隆20181024处理where.sqlString bug===>start */
  private String sqlString = " 1 = 1";

  public String getSqlString() {
    return sqlString;
  }

  public void setSqlString(String sqlString) {
    this.sqlString = sqlString;
  }

  public void addWfl(WhereFieldItem fieldTtem) {
    StringBuilder builder = new StringBuilder(this.sqlString + " AND ");
    fieldTtem.doAddWhere("", builder);
    this.sqlString = builder.toString();
  }
  /** 吴佳隆20181024处理where.sqlString bug===>end */
  public String toSqlString() {
    return toSqlString("a.");
  }

  static String toSQLString(Object value) {
    if (value == null) return "null";
    else if (value instanceof String) return "'" + value.toString() + "'";
    else if (value instanceof Date)
      return "'"
          + DateTimeUtil.formatDateToString((Date) value, DateTimeUtil.DATE_TIME_FORMART_ALL)
          + "'";
    else return value.toString();
  }

  public String toSqlString(String alias) {
    StringBuilder sb = new StringBuilder();
    for (WhereFieldItem where : this) {
      if (sb.length() > 0) {
        sb.append("\n");
        sb.append(" and ");
      }
      where.doAddWhere(alias, sb);
    }
    for (WhereFilterItem filter : filters) {
      if (sb.length() > 0) {
        sb.append("\n");
        sb.append(" and ");
      }
      filter.addWhere(sb);
    }
    return sb.toString();
  }

  public void addFilter(String filter, Object... args) {
    this.filters.add(new WhereFilterItem(filter, args));
  }
}
