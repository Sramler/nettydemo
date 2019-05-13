package com.tymls.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.tymls.server.ISRuntimeException;
import com.tymls.server.vo.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JSONUtil {

  private JSONUtil() {}

  static int JSON_Feature;

  static {
    int features = 0;
    if (AppConstant.IS_RUNTIME) {
      features |= SerializerFeature.QuoteFieldNames.getMask();
      features |= SerializerFeature.SkipTransientField.getMask();
      features |= SerializerFeature.WriteEnumUsingName.getMask();
      features |= SerializerFeature.SortField.getMask();
      // features |= SerializerFeature.PrettyFormat.getMask();
      features |= SerializerFeature.SkipTransientField.getMask();
      // features |= SerializerFeature.SortField.getMask();
    } else {
      features |= SerializerFeature.QuoteFieldNames.getMask();
      features |= SerializerFeature.SkipTransientField.getMask();
      features |= SerializerFeature.WriteEnumUsingName.getMask();
      //			features |= SerializerFeature.SortField.getMask();
      //			features |= SerializerFeature.PrettyFormat.getMask();
      features |= SerializerFeature.SkipTransientField.getMask();
      features |= SerializerFeature.SortField.getMask();
    }
    JSON_Feature = features;
    // JSON.DEFAULT_GENERATE_FEATURE = JSON.DEFAULT_GENERATE_FEATURE ^
    // SerializerFeature.QuoteFieldNames.getMask() | ;
  }

  public static Object loadFromFile(String filename, Class<?> clazz) {
    String text = loadStringFromFile(filename);
    return JSON.parseObject(text, clazz);
  }

  public static String loadStringFromFile(String fileName) {
    StringBuilder sb = new StringBuilder();
    List<String> list;
    try {
      list = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new ISRuntimeException(e.getMessage(), e);
    }
    for (String s : list) {
      sb.append(s);
      sb.append("\n");
    }
    return sb.toString();
  }

  public static String beanToString(JSONObject obj) {
    return JSONObject.toJSONString(obj, JSON_Feature);
  }

  public static <T> String listToString(List<T> list) {
    return JSONArray.toJSONString(list, JSON_Feature);
  }

  public static String toJSONString(Object obj) {
    return JSON.toJSONString(obj, JSON_Feature);
  }

  public static void saveToFile(String fileName, String text) {
    File file = new File(fileName);
    if (!file.exists()) {
      makeDir(file.getParentFile());
    }
    FileWriter fw = null;
    try {
      fw = new FileWriter(file);
      fw.write(text);
      fw.close();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new ISRuntimeException(e.getMessage(), e);
    }
  }

  private static void makeDir(File dir) {
    if (!dir.getParentFile().exists()) {
      makeDir(dir.getParentFile());
    }
    dir.mkdir();
  }

  public static void saveToFile(String fileName, Object object) {
    String jsonStr = JSON.toJSONString(object, JSON_Feature);
    saveToFile(fileName, jsonStr);
  }

  public static interface BeanMapperToJson {
    JSONObject map(Object from, JSONObject to);
  }

  public static JSONObject toJSON(Object from, BeanMapperToJson mapper) {
    if (from == null) return null;
    JSONObject to = new JSONObject();
    mapper.map(from, to);
    return to;
  }

  public static JSONArray toArray(List<?> froms, BeanMapperToJson mapper) {
    JSONArray result = new JSONArray();
    for (Object from : froms) {
      JSONObject to = new JSONObject();
      to = mapper.map(from, to);
      result.fluentAdd(to);
    }
    return result;
  }

  public static JSONObject toJSON(String str) {
    return JSON.parseObject(str);
  }

  public static JSONObject toJSON(Object bean) {
    return (JSONObject) JSON.toJSON(bean);
  }

  public static JSONArray toJSONArray(String json) {
    return JSONArray.parseArray(json);
  }

  public static <T> T parseObject(String text, Class<T> clazz) {
    return (T) JSON.parseObject(text, clazz);
  }

  public static <T> T toObject(JSONObject json, Class<T> clazz) {
    /// TODO luke
    return (T) JSON.parseObject(json.toJSONString(), clazz);
  }

  public static <T> List<T> parseArray(String text, Class<T> clzz) {
    return JSONArray.parseArray(text, clzz);
  }

  /**
   * 将url参数转换成map
   *
   * @param param aa=11&bb=22&cc=33
   * @return
   */
  public static Map<String, Object> getUrlParams(String param) {
    Map<String, Object> map = new HashMap<String, Object>(0);
    if (StringUtils.isBlank(param)) {
      return map;
    }
    String[] params = param.split("&");
    for (int i = 0; i < params.length; i++) {
      String[] p = params[i].split("=");
      if (p.length == 2) {
        map.put(p[0], p[1]);
      }
    }
    return map;
  }

  /**
   * 将map转换成url
   *
   * @param map
   * @return
   */
  public static String getUrlParamsByMap(Map<String, Object> map) {
    if (map == null) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      sb.append(entry.getKey() + "=" + entry.getValue());
      sb.append("&");
    }
    String s = sb.toString();
    if (s.endsWith("&")) {
      s = StringUtils.substringBeforeLast(s, "&");
    }
    return s;
  }

  /**
   * 对象转Map
   *
   * @param obj
   * @return
   */
  public static Map<?, ?> objectToMap(Object obj) {
    if (obj == null) return null;
    return new BeanMap(obj);
  }
}
