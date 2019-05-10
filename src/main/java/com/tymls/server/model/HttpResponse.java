package com.tymls.server.model;

import com.alibaba.fastjson.JSONObject;
import com.tymls.util.JSONUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * HTML 请求返回的结果，格式
 *
 * @author luke
 */
public class HttpResponse {

  public static int OK = 0;
  public static int ErrorWithMsg = -1;
  public static int NeedConfirm = -2;
  public static int FatalError = -3;
  public static int NeedLogin = -4;
  public static int Hint = -5;
  public static int HasNoPermission = -6;
  public static int OldLogin = -7;

  private int code;
  private String msg;
  private Object data;
  private Object exData;
  private JSONObject jsonObject;

  public HttpResponse() {
    code = 0;
    msg = null;
    exData = null;
    jsonObject = new JSONObject(true);
    jsonObject.put("code", this.code);
    jsonObject.put("msg", this.msg);
  }

  public String toString() {
    return JSONUtil.beanToString(this.jsonObject);
  }

  public void addResult(int retCode, String msg) {
    this.code = retCode;
    this.msg = msg;
    jsonObject.put("code", this.code);
    if (StringUtils.isNotBlank(msg)) jsonObject.put("msg", this.msg);
    else jsonObject.remove("msg");
  }

  public void assignData(Object value) {
    this.data = value;
    if (data != null) jsonObject.put("data", data);
    else jsonObject.remove("data");
  }

  public void assignExData(Object value) {
    this.exData = value;
    if (exData != null) jsonObject.put("exData", value);
    else jsonObject.remove("exData");
  }

  public static String getResultJson(Throwable cause) {
    return getResultJson(FatalError, cause);
  }

  public static String getResultJson(int code, Throwable cause) {
    JSONObject json = new JSONObject();
    json.put("code", code);
    json.put("msg", cause.getMessage());
    return JSONUtil.beanToString(json);
  }

  public static String getResultJson(int code, String msg) {
    JSONObject json = new JSONObject();
    json.put("code", code);
    json.put("msg", msg);
    return JSONUtil.beanToString(json);
  }
}
