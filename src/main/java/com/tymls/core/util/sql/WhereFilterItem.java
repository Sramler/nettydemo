package com.tymls.core.util.sql;

public class WhereFilterItem {
  String filter;
  Object[] args;

  public WhereFilterItem(String filter, Object... values) {
    this.filter = filter;
    this.args = values;
  }

  void addWhere(StringBuilder sb) {
    String s = this.filter;
    for (int i = 0; i < this.args.length; i++) {
      s.replace("{" + i + 1 + "}", WhereList.toSQLString(this.args[i]));
    }
    sb.append(s);
  }
}
