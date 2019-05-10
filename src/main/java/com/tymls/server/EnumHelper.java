package com.tymls.server;

import com.tymls.server.vo.IDAndName;

import java.util.*;

/** @author luke */
@SuppressWarnings("rawtypes")
public class EnumHelper {

  static {
    allIDEnumMap = new HashMap<Class, Map<Integer, Object>>();
    allIdNameMap = new HashMap<Class, Map<Integer, String>>();
  }

  private static Map<Class, Map<Integer, Object>> allIDEnumMap;

  private static Map<Class, Map<Integer, String>> allIdNameMap;

  public static List<IDAndName> getIDNameList(Class<?> clzz) {
    List<IDAndName> list = new ArrayList<IDAndName>();
    Map<Integer, String> map = getIDNameMap(clzz);
    for (Map.Entry<Integer, String> entry : map.entrySet()) {

      list.add(new IDAndName(entry.getKey(), entry.getValue()));
    }
    Collections.sort(list, new IDNameCompare());
    return list;
  }

  public static List<IDAndName> getList(String clzzName) {
    try {
      return getIDNameList(Class.forName(clzzName));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return new ArrayList<IDAndName>();
  }

  public static class IDNameCompare implements Comparator<IDAndName> {
    public int compare(IDAndName o1, IDAndName o2) {
      return o1.getId() - o2.getId();
    }
  }

  public static Map<Integer, Object> getIDEnumMap(Class<?> clzz) {
    return allIDEnumMap.get(clzz);
  }

  public static Map<Integer, String> getIDNameMap(Class<?> clzz) {
    return allIdNameMap.get(clzz);
  }

  public static void register(Class<?> clzz, Object enumValue, int id, String name) {
    Map<Integer, Object> idEnumMap = allIDEnumMap.get(clzz);
    if (idEnumMap == null) {
      idEnumMap = new HashMap<Integer, Object>();
      allIDEnumMap.put(clzz, idEnumMap);
    }
    idEnumMap.put(id, enumValue);

    Map<Integer, String> idNameMap = allIdNameMap.get(clzz);
    if (idNameMap == null) {
      idNameMap = new HashMap<Integer, String>();
      allIdNameMap.put(clzz, idNameMap);
    }
    idNameMap.put(id, name);
  }

  public static Object forValue(Class<?> clzz, String value) {
    if (value == null || value == "") return null;
    else return getIDEnumMap(clzz).get(Integer.parseInt(value));
  }

  public static Object forValue(Class<?> clzz, int value) {
    return getIDEnumMap(clzz).get(value);
  }

  public static Object forValue(Class<?> clzz, Integer value) {
    if (value == null) return null;
    else return getIDEnumMap(clzz).get(value);
  }

  public static String forText(Class<?> clzz, String value) {
    if (value == null || value == "") return "";
    else {
      return getIDNameMap(clzz).get(Integer.parseInt(value));
    }
  }

  public static String forText(Class<?> clzz, int value) {
    return getIDNameMap(clzz).get(value);
  }

  public static String forText(Class<?> clzz, Integer value) {
    if (value == null) return null;
    else return getIDNameMap(clzz).get(value);
  }

  private static Map<Integer, Boolean> twoPowerMap;

  public static String forTexts(Class<?> clzz, int value, String seprateStr) {
    Map<Integer, String> valueNames = getIDNameMap(clzz);
    if (null == valueNames) return null;
    StringBuilder sb = new StringBuilder();
    if (null == twoPowerMap) {
      twoPowerMap = new LinkedHashMap<Integer, Boolean>();
      int ii = 1;
      for (int i = 0; i < 31; i++) {
        twoPowerMap.put(ii, true);
        ii *= 2;
      }
    }
    for (Map.Entry<Integer, String> item : valueNames.entrySet()) {
      if ((value & item.getKey()) != 0) {
        if (null == twoPowerMap.get(item.getKey())) {
          value = value - item.getKey();
          if (sb.length() > 0) sb.append(seprateStr);
          sb.append(item.getValue());
        }
      }
    }
    for (Map.Entry<Integer, String> item : valueNames.entrySet()) {
      if ((value & item.getKey()) != 0) {
        value = value - item.getKey();
        if (sb.length() > 0) sb.append(seprateStr);
        sb.append(item.getValue());
      }
    }
    return sb.toString();
  }

  public static void main(String[] args) {}
}
