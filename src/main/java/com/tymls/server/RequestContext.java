package com.tymls.server;

import com.alibaba.fastjson.JSONObject;
import com.tymls.core.util.sql.Pager;
import com.tymls.core.util.sql.SqlOrderByItem;
import com.tymls.core.util.sql.SqlOrderItemType;
import com.tymls.server.common.JsonBodyContants;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理http传入的参数
 *
 * <p>1.根据网址解析对应的handler处理器及方法
 *
 * <p>2.提供网址参数，body-json参数解析 body-form参数解析 -body-file参数解析，并将这三种解析合并处理，对调用者透明
 *
 * @author luke
 */
@Slf4j
public class RequestContext {

  private static final HttpDataFactory factory =
      new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
  // private static IFSService fSService;
  // private static SysLogService sysLogService;
  // static ApplicationContext applicationContext;

  static {
    // applicationContext = (ApplicationContext) (AppConstant.ApplicationContext);
  }

  private HttpHandlerContext parent;
  private JSONObject postJSON;
  private String _requestBody;
  private String[] urlSections;
  private QueryStringDecoder queryStringDecoder;
  private Map<String, byte[]> fileContents;

  public RequestContext(HttpHandlerContext parent) {
    this.parent = parent;
    this.urlSections =
        parent.getRequest().uri().substring(HttpHandlerContext.BASEURL.length()).split("/");
    this.queryStringDecoder = new QueryStringDecoder(parent.getRequest().uri());
  }

  public String getHandlerName() {
    if (urlSections.length < 3) {
      throw new ISRuntimeException(
          "访问url %s 错误，格式应该是%smm/user/query?userId=123", this.getUri(), HttpHandlerContext.BASEURL);
    }
    return urlSections[0] + "/" + urlSections[1] + "/";
  }

  public String getMethodName() {
    if (urlSections.length < 3) {
      throw new ISRuntimeException(
          "访问url %s 错误，格式应该是%smm/user/query?userId=123", this.getUri(), HttpHandlerContext.BASEURL);
    }
    String ss1 = urlSections[2];
    int start = ss1.indexOf('?');
    if (start < 0) {
      return ss1;
    } else {
      return ss1.substring(0, start);
    }
  }

  private QueryStringDecoder formStringDecoder;

  public Map<String, String> getFormData() {
    if (formStringDecoder == null) {
      formStringDecoder = new QueryStringDecoder(this.getRequestBody());
    }
    HashMap<String, String> result2 = decoderToMap(this.formStringDecoder);
    return result2;
  }

  private HashMap<String, String> decoderToMap(QueryStringDecoder decoder) {
    Map<String, List<String>> result1 = decoder.parameters();
    HashMap<String, String> result2 = new HashMap<String, String>();
    for (Map.Entry<String, List<String>> item : result1.entrySet()) {
      String ss = null;
      for (String s : item.getValue()) {
        if (ss != null) ss += "," + s;
        else ss = s;
      }
      result2.put(item.getKey(), ss);
    }
    return result2;
  }

  public boolean hasParam(String paramName) {
    return this.queryStringDecoder.parameters().get(paramName) != null;
  }

  public String getParam(String paramName) {
    List<String> values = this.queryStringDecoder.parameters().get(paramName);
    if (values == null || values.size() == 0 && values.get(0).equals("")) {
      throw new ISRuntimeException("在网址%s中必须传入名称为 %s的参数", this.getUri(), paramName);
    }
    return values.get(0);
  }

  public String getParam(String paramName, String defaultValue) {
    List<String> values = this.queryStringDecoder.parameters().get(paramName);
    if (values == null || values.size() == 0 && values.get(0).equals("")) {
      return defaultValue;
    } else {
      return values.get(0);
    }
  }

  public List<SqlOrderByItem> getOrderBy(
      String defaultOrder, SqlOrderItemType defaultOrderItemType) {
    // 按参数order进行排序
    List<SqlOrderByItem> orderByList = new ArrayList<>();
    String orderDesc_ = this.getParam(JsonBodyContants.ORDER_DESC_, null);
    SqlOrderByItem orderItem = new SqlOrderByItem();
    String order = this.getParam(JsonBodyContants.ORDER_, null);
    SqlOrderItemType orderItemType = null;
    if (orderDesc_ == null || orderDesc_.equals("")) { // 不传，使用默认
      orderItemType = SqlOrderItemType.DEFAULT;
    } else {
      orderItemType = SqlOrderItemType.valueOf(orderDesc_);
    }
    switch (orderItemType) {
      case DESC:
        orderItem.setField(order);
        orderItem.setValue(SqlOrderItemType.DESC);
        break;
      case ASC:
        orderItem.setField(order);
        orderItem.setValue(SqlOrderItemType.ASC);
        break;
      default:
        orderItem.setField(defaultOrder);
        orderItem.setValue(defaultOrderItemType);
        break;
    }
    orderByList.add(orderItem);
    return orderByList;
  }

  public Pager getPager() {
    String pageIndex = this.getParam(JsonBodyContants.PAGEINDEX_NODE);
    String pageSize = this.getParam(JsonBodyContants.PAGESIZE_NODE);
    return new Pager(pageIndex, pageSize);
  }

  public Pager getPostPager() {
    String pageIndex = this.getPostStringParam(JsonBodyContants.PAGEINDEX_NODE);
    String pageSize = this.getPostStringParam(JsonBodyContants.PAGESIZE_NODE);
    return new Pager(pageIndex, pageSize);
  }

  public String getUri() {
    return this.parent.getRequest().uri();
  }

  /** 获取X-Real-IP */
  private static String getXRealIp(Channel channel, FullHttpRequest request) {
    String ip = null;
    if (request.headers().contains("x-real-ip")) {
      ip = request.headers().get("x-real-ip");
    } else {
      ip = channel.remoteAddress().toString();
    }
    if (StringUtils.isNotEmpty(ip) && ip.startsWith("/")) {
      if (ip.indexOf(":") != -1) {
        ip = StringUtils.substringBetween(ip, "/", ":");
      } else {
        ip = StringUtils.substring(ip, 1);
      }
    }
    return ip;
  }

  public String getRemoteIp() {
    return getXRealIp(this.parent.getChannelContext().channel(), this.parent.getRequest());
  }

  public boolean hasPostParam(String paramName) {
    JSONObject obj = this.getPostJson();
    return obj.containsKey(paramName);
  }

  public Object getPostParam(String paramName, Object defaultValue) {
    JSONObject obj = this.getPostJson();
    Object result = obj.get(paramName);
    if (result == null) return defaultValue;
    else return result;
  }

  public String getPostStringParam(String paramName, String defaultValue) {
    Object result = getPostParam(paramName, defaultValue);
    if (result == null) return null;
    else return result.toString();
  }

  /**
   * 读取参数的值，注意必须在json中存在
   *
   * @param paramName
   * @return
   */
  public Object getPostParam(String paramName) {
    JSONObject obj = this.getPostJson();
    Object result = obj.get(paramName);
    if (result == null) {
      throw new ISRuntimeException(
          "网址(%s)传入参数错误！！  必须在使用 post请求， 必须包含名称为(%s)的参数", this.getUri(), paramName);
    }
    return result;
  }

  /**
   * 读取参数的字符值，注意必须在json中存在
   *
   * @param paramName
   * @return
   */
  public String getPostStringParam(String paramName) {
    JSONObject obj = this.getPostJson();
    Object result = obj.get(paramName);
    if (result == null)
      throw new ISRuntimeException(
          "网址(%s)传入参数错误！！  必须在使用 post请求， 必须包含名称为(%s)的参数", this.getUri(), paramName);
    else return result.toString();
  }

  /**
   * 读取参数的值，注意必须在json中存在
   *
   * <p>exampe: {name:"luke", wife:{name:"Ally", age:35}}
   *
   * <p>getPostParamByNames("name") // "luke"
   *
   * <p>getJsonValue("wife","name"); // "Ally"
   *
   * @param names
   * @return
   */
  @SuppressWarnings("unchecked")
  public Object getPostParamByNames(String... names) {
    JSONObject obj = this.getPostJson();
    Object value = obj;
    for (String name : names) {
      value = ((Map<String, Object>) value).get(name);
      if (value == null) {
        throw new ISRuntimeException(
            "网址(%s)传入参数错误！！  必须在使用 post请求， 必须包含名称为(%s)的参数", this.getUri(), name);
      }
    }
    return value;
  }

  /**
   * 读取参数的值，注意必须在json中存在
   *
   * <p>exampe: {name:"luke", wife:{name:"Ally", age:35}}
   *
   * <p>getPostParamByNames("name") // "luke"
   *
   * <p>getJsonValue("wife","name"); // "Ally"
   *
   * @param names
   * @return
   */
  public String getPostParamStringByNames(String... names) {
    return getPostParamByNames(names).toString();
  }

  //  public void processUploadFiles() {
  //    HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory,
  // this.parent.getRequest());
  //    if (!decoder.isMultipart()) {
  //      throw new ISRuntimeException("客户端没有没有上传文件，不能读取保存文件(网址url-%s) ", this.getUri());
  //    }
  //    Map<String, byte[]> contents = new HashMap<String, byte[]>();
  //    JSONObject result = new JSONObject();
  //    try {
  //      while (decoder.hasNext()) {
  //        InterfaceHttpData data = decoder.next();
  //        if (data != null) {
  //          try {
  //            handleBodyWithFiles(getFSServer(), data, result, contents);
  //          } finally {
  //            data.release();
  //          }
  //        }
  //      }
  //    } catch (EndOfDataDecoderException e) {
  //      e.printStackTrace();
  //      log.error("分片读取数据时发生错误", e);
  //      throw new ISRuntimeException("上传文件失败，不能读取保存文件(网址url-%s) ", this.getUri());
  //    }
  //
  //    this.postJSON = result;
  //    decoder.cleanFiles();
  //    this._requestBody = postJSON.toJSONString();
  //    this.fileContents = contents;
  //    return;
  //  }

  public byte[] getFileContent(String name) {
    if (this.fileContents == null) throw new ISRuntimeException("必须先调用FileProcess 然后才能读取数据");
    return this.fileContents.get(name);
  }

  //  /**
  //   * 处理(单字段多文件)文件上传
  //   *
  //   * @author luke
  //   */
  //  public boolean processArrUploadFiles() {
  //    HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory,
  // this.parent.getRequest());
  //    if (!decoder.isMultipart()) {
  //      // 客户端没有没有上传文件，不能读取保存文件
  //      return false;
  //    }
  //    JSONObject result = new JSONObject();
  //    List<Map<String, Object>> fileResults = new ArrayList<Map<String, Object>>();
  //    try {
  //      while (decoder.hasNext()) {
  //        InterfaceHttpData data = decoder.next();
  //        if (data.getHttpDataType() == HttpDataType.Attribute
  //            || data.getHttpDataType() == HttpDataType.InternalAttribute) { // 接收json字符串
  //          Attribute attribute = (Attribute) data;
  //          String value = null;
  //          try {
  //            String name = attribute.getName();
  //            value = attribute.getValue();
  //            result.put(name, value);
  //          } catch (IOException e) {
  //            e.printStackTrace();
  //            LogUtil.error(
  //                "BODY Attribute: "
  //                    + attribute.getHttpDataType().name()
  //                    + ":"
  //                    + attribute.getName()
  //                    + " Error while reading value: "
  //                    + e.getMessage(),
  //                e);
  //          }
  //        } else if (data.getHttpDataType() == HttpDataType.FileUpload) { // 接收json字符串
  //          if (data != null) {
  //            try {
  //              Map<String, Object> fileResult =
  //                  FileUploadHelper.readFileContent(getFSServer(), (FileUpload) data);
  //              fileResult.remove("fileContent");
  //              fileResults.add(fileResult);
  //            } catch (Exception e) {
  //              e.printStackTrace();
  //              LogUtil.error("从缓存中读取数据并上传服务器时发生错误", e);
  //            } finally {
  //              data.release();
  //            }
  //          }
  //        }
  //      }
  //      if (fileResults != null && fileResults.size() >= 1) {
  //        String fileName = (String) fileResults.get(0).get("name");
  //        result.put(fileName, fileResults);
  //      }
  //    } catch (EndOfDataDecoderException e) {
  //      e.printStackTrace();
  //      LogUtil.error("分片读取数据时发生错误", e);
  //    }
  //
  //    this.postJSON = result;
  //    decoder.cleanFiles();
  //    this._requestBody = postJSON.toJSONString();
  //    return true;
  //  }

  //  /**
  //   * 处理(多字段多文件)文件上传
  //   *
  //   * @author luke
  //   */
  //  @SuppressWarnings("unchecked")
  //  public boolean processMultiUpload() {
  //    HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(this.parent.getRequest());
  //    if (!decoder.isMultipart()) {
  //      // 客户端没有没有上传文件，不能读取保存文件
  //      return false;
  //    }
  //    JSONObject result = new JSONObject();
  //    try {
  //      while (decoder.hasNext()) {
  //        InterfaceHttpData data = decoder.next();
  //        if (data.getHttpDataType() == HttpDataType.Attribute
  //            || data.getHttpDataType() == HttpDataType.InternalAttribute) { // 接收json字符串
  //          Attribute attribute = (Attribute) data;
  //          String value = null;
  //          try {
  //            String name = attribute.getName();
  //            value = attribute.getValue();
  //            result.put(name, value);
  //          } catch (IOException e) {
  //            e.printStackTrace();
  //            LogUtil.error(
  //                "BODY Attribute: "
  //                    + attribute.getHttpDataType().name()
  //                    + ":"
  //                    + attribute.getName()
  //                    + " Error while reading value: "
  //                    + e.getMessage(),
  //                e);
  //          }
  //        } else if (data.getHttpDataType() == HttpDataType.FileUpload) { // 接收json字符串
  //          if (data != null) {
  //            try {
  //              Map<String, Object> fileResult =
  //                  FileUploadHelper.readFileContent(getFSServer(), (FileUpload) data);
  //              fileResult.remove("fileContent");
  //
  //              String filedName = (String) fileResult.get("name");
  //              if (result.containsKey(filedName)) {
  //                List<Map<String, Object>> filedFiles =
  //                    (List<Map<String, Object>>) result.get(filedName);
  //                filedFiles.add(fileResult);
  //              } else {
  //                List<Map<String, Object>> filedFiles = new ArrayList<Map<String, Object>>();
  //                filedFiles.add(fileResult);
  //                result.put(filedName, filedFiles);
  //              }
  //            } catch (Exception e) {
  //              e.printStackTrace();
  //              LogUtil.error("从缓存中读取数据并上传服务器时发生错误", e);
  //            } finally {
  //              data.release();
  //            }
  //          }
  //        }
  //      }
  //    } catch (EndOfDataDecoderException e) {
  //      e.printStackTrace();
  //      LogUtil.error("分片读取数据时发生错误", e);
  //    }
  //
  //    this.postJSON = result;
  //    decoder.cleanFiles();
  //    this._requestBody = postJSON.toJSONString();
  //    return true;
  //  }

  //  /**
  //   * By luke
  //   *
  //   * @param fSService
  //   * @param data
  //   * @param result
  //   */
  //  private static void handleBodyWithFiles(
  //      IFSService fSService,
  //      InterfaceHttpData data,
  //      Map<String, Object> result,
  //      Map<String, byte[]> contents) {
  //    /** HttpDataType有三种类型 Attribute, FileUpload, InternalAttribute */
  //    if (data.getHttpDataType() == HttpDataType.Attribute
  //        || data.getHttpDataType() == HttpDataType.InternalAttribute) { // 接收json字符串
  //      Attribute attribute = (Attribute) data;
  //      String value = null;
  //      try {
  //        String name = attribute.getName();
  //        value = attribute.getValue();
  //        result.put(name, value);
  //      } catch (IOException e) {
  //        e.printStackTrace();
  //        LogUtil.error(
  //            "BODY Attribute: "
  //                + attribute.getHttpDataType().name()
  //                + ":"
  //                + attribute.getName()
  //                + " Error while reading value: "
  //                + e.getMessage(),
  //            e);
  //      }
  //    } else if (data.getHttpDataType() == HttpDataType.FileUpload) { // 接收json字符串
  //      FileUpload fileUpload = (FileUpload) data;
  //      if (!fileUpload.isCompleted()) {
  //        LogUtil.debug("\tFile to be continued but should not!\r\n");
  //        return;
  //      }
  //
  //      if (!fileUpload.getFilename().contains(".apk")
  //          && !fileUpload.getFilename().contains(".bin")) {
  //        if (fileUpload.length() > 1024 * 1024 * 100) { // TODO 暂定为上传文件不能大于1M
  //          LogUtil.warning("\tFile too long to be printed out:" + fileUpload.length() + "\r\n");
  //          throw new ISRuntimeException(ResponseCode.Error, "图片尺寸不能大于1M");
  //          // return;
  //        }
  //      }
  //      LogUtil.debug("start upload...");
  //      Map<String, Object> fileInfo = null;
  //      try {
  //        fileInfo = FileUploadHelper.readFileContent(fSService, fileUpload);
  //      } catch (Exception e) {
  //        e.printStackTrace();
  //        LogUtil.error("从缓存中读取数据并上传服务器时发生错误", e);
  //        throw new ISRuntimeException(ResponseCode.Bug, e.getMessage());
  //      }
  //      // result.put(fileUpload.getName()+"__content", fileInfo.get("fileContent"));
  //      contents.put(fileUpload.getName(), (byte[]) fileInfo.get("fileContent"));
  //      fileInfo.remove("fileContent");
  //      result.put(fileUpload.getName(), fileInfo);
  //    }
  //  }

  //  private IFSService getFSServer() {
  //    if (fSService == null) fSService = (IFSService) applicationContext.getBean("fSService");
  //    return fSService;
  //  }

  public String getRequestBody() {
    if (this._requestBody == null) {
      this._requestBody = this.parent.getRequest().content().toString(CharsetUtil.UTF_8); // 获取请求的内容
    }
    return this._requestBody;
  }
  /**
   * 返回提交参数中根据Post传入的json结果
   *
   * @return
   */
  public JSONObject getPostJson() {
    return this.getPostJson(true);
  }

  /**
   * 返回提交参数中根据Post传入的json结果
   *
   * @return
   */
  public JSONObject getPostJson(boolean checkExits) {
    if (this.postJSON == null) {
      String body = this.parent.getRequest().content().toString(CharsetUtil.UTF_8);
      log.info("请求参数：" + body);
      if (StringUtils.isNotBlank(body)) {
        this.postJSON = JSONObject.parseObject(body);
      } else if (checkExits) {
        throw new ISRuntimeException("客户端访问网址 (url-%s) 没有传入json数据，不能读取Json数据", this.getUri());
      } else {
        return null;
      }
    }
    return this.postJSON;
  }

  //  public SysLogService getSysLogService() {
  //    if (sysLogService == null)
  //      sysLogService = (SysLogService) applicationContext.getBean("sysLogService");
  //    return sysLogService;
  //  }

  public HashMap<String, String> getParamMap() {
    HashMap<String, String> result2 = decoderToMap(this.queryStringDecoder);
    JSONObject result = getPostJson(false);
    if (result != null) {
      for (Map.Entry<String, Object> item : result.entrySet()) {
        result2.put(item.getKey(), item.getValue().toString());
      }
    }
    return result2;
  }
}
