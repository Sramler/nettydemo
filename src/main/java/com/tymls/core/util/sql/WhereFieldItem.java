package com.tymls.core.util.sql;

import java.util.ArrayList;
import java.util.List;

public class WhereFieldItem {
  String fieldName;
  ConditionOperator operator;
  Object value;

  public WhereFieldItem(String fieldName, ConditionOperator operator, Object value) {
    this.fieldName = fieldName;
    this.operator = operator;
    this.value = value;
  }

  public WhereFieldItem(String fieldName, Object value) {
    this.fieldName = fieldName;
    this.operator = ConditionOperator.EQ;
    this.value = value;
  }

  public static List<WhereFieldItem> build(String fieldNameEx, Object value) {
    String[] ss = fieldNameEx.split("__");
    List<WhereFieldItem> result = new ArrayList<WhereFieldItem>();
    if (ss.length == 1) result.add(new WhereFieldItem(fieldNameEx, ConditionOperator.EQ, value));
    else if (ss.length == 2) {
      String fieldName = ss[0];
      String opStr = ss[1];
      ConditionOperator op = Enum.valueOf(ConditionOperator.class, opStr);
      result.add(new WhereFieldItem(fieldName, op, value));
    } else {
      throw new RuntimeException(
          String.format("fieldNameEx with operator is error (%s)", fieldNameEx));
    }
    return result;
  }

  void doAddWhere(String alias, StringBuilder sb) {
    sb.append(alias);
    sb.append(this.fieldName);
    String sqlValue;
    switch (this.operator) {
      case EQ:
        sqlValue = WhereList.toSQLString(this.value);
        if (this.value != null) sb.append(" = ");
        else sb.append(" is ");
        sb.append(sqlValue);
        break;
      case NE:
        sqlValue = WhereList.toSQLString(this.value);
        sb.append(" <> ");
        sb.append(sqlValue);
        break;
      case GE:
        sqlValue = WhereList.toSQLString(this.value);
        sb.append(" >= ");
        sb.append(sqlValue);
        break;
      case GT:
        sqlValue = WhereList.toSQLString(this.value);
        sb.append(" > ");
        sb.append(sqlValue);
        break;
      case LE:
        sqlValue = WhereList.toSQLString(this.value);
        sb.append(" <= ");
        sb.append(sqlValue);
        break;
      case LT:
        sqlValue = WhereList.toSQLString(this.value);
        sb.append(" < ");
        sb.append(sqlValue);
        break;
      case LIKE:
        sqlValue = WhereList.toSQLString(this.value);
        sb.append(" like ");
        sb.append(sqlValue);
        break;
      case INCLUDE:
        sqlValue = WhereList.toSQLString(this.value);
        sb.append(" like ");
        sb.append("%" + sqlValue + "%");
        break;
      case START_WITH:
        sqlValue = WhereList.toSQLString(this.value);
        sb.append(" like ");
        sb.append(sqlValue + "%");
        break;
      case END_WITH:
        sqlValue = WhereList.toSQLString(this.value);
        sb.append(" like ");
        sb.append("%" + sqlValue);
        break;
      case BETWEEN:
        if (this.value instanceof Object[]) {
          Object[] values = (Object[]) this.value;
          if (values.length != 2) throw new RuntimeException("条件错误,between 参数必须是2个数值");
          sb.append(" between ");
          sqlValue = WhereList.toSQLString(values[0]);
          sb.append(sqlValue);
          sb.append(" and ");
          sqlValue = WhereList.toSQLString(values[1]);
          sb.append(sqlValue);
        } else {
          sb.append(" >= ");
          sqlValue = WhereList.toSQLString(this.value);
        }
        break;
      case IN:
        if (this.value instanceof Object[]) {
          sb.append(" in ");
          sb.append("(");
          Object[] values = (Object[]) this.value;
          for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(",");
            sqlValue = WhereList.toSQLString(values[i]);
            sb.append(sqlValue);
          }
          sb.append(")");
        } else {
          sb.append(" = ");
          sqlValue = WhereList.toSQLString(this.value);
        }
        break;
      default:
        sqlValue = WhereList.toSQLString(this.value);
        sb.append(" = ");
        sb.append("%" + sqlValue);
    }
  }
}
