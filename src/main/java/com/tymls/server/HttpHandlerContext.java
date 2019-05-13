package com.tymls.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.tymls.core.util.sql.Pager;
import com.tymls.core.util.sql.SqlOrderBy;
import com.tymls.core.util.sql.SqlOrderByItem;
import com.tymls.core.util.sql.SqlOrderItemType;
import com.tymls.server.model.HttpResponse;
import com.tymls.server.model.QueryContext;
import com.tymls.server.vo.AppConstant;
import com.tymls.util.JSONUtil;
import com.tymls.util.NettyTool;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
public class HttpHandlerContext {

  public static String BASEURL = "/m/";

  public static String Page_Index_ParamName = "__pageIndex";
  public static String Page_Size_ParamName = "__pageSize";

  @Getter protected transient ChannelHandlerContext channelContext;
  @Getter private FullHttpRequest request;
  private HttpResponse response;

  private transient RequestContext requestContext;

  @Getter private int responseCount = 0;

  private transient long startRunAt;
  private transient FullHttpResponse httpResponse;

  private QueryStringDecoder formStringDecoder;

  public HttpHandlerContext(ChannelHandlerContext ctx, FullHttpRequest req) {
    this.channelContext = ctx;
    this.request = req;
    this.response = new HttpResponse();
    this.requestContext = new RequestContext(this);
    this.startRunAt = System.currentTimeMillis();
  }

  public String getMethodName() {
    return this.requestContext.getMethodName();
  }

  public Object getUri() {
    return this.request.uri();
  }

  /**
   * 返回请求头中的origin信息
   *
   * @return
   */
  public String getOrigin() {
    String origin = this.request.headers().get("Origin");
    return origin;
  }

  /**
   * 将数据写回客户端
   *
   * @param content 文字内容
   */
  public void writeToChannel(String content) {
    responseCount++;
    ByteBuf buff = null;
    try {
      buff = Unpooled.wrappedBuffer(content.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    Channel channel = this.channelContext.channel();
    FullHttpResponse response = getHttpResponse();
    response.content().writeBytes(buff);
    buff.release();
    if (!channel.isOpen() || !channel.isWritable()) {
      ByteBuf buf = Unpooled.copiedBuffer(httpResponse.status().toString(), CharsetUtil.UTF_8);
      response.content().writeBytes(buf);
      buf.release();
      HttpUtil.setContentLength(httpResponse, httpResponse.content().readableBytes());
    } else {
      ChannelFuture future = channel.writeAndFlush(httpResponse);
      future.addListener(ChannelFutureListener.CLOSE);
    }
    ChannelFuture future = this.channelContext.channel().writeAndFlush(httpResponse);
    future.addListener(ChannelFutureListener.CLOSE);
    long endRunAt = System.currentTimeMillis();
    // LogUtil.info("返回参数：" +content);
    log.info("END:" + this.getUri() + "调用用时：" + (endRunAt - startRunAt) + "毫秒");
    if (!AppConstant.IS_RUNTIME) {
      // TODO 设置模式为debug DEBUG
      // log.debug("RETURN:" + content);
    }
  }

  public FullHttpResponse getHttpResponse() {
    if (httpResponse == null) {
      httpResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
      httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
      if (this.getOrigin() != null) {
        httpResponse.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, this.getOrigin());
        httpResponse.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
      } else {
        httpResponse.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      }
    }
    return httpResponse;
  }

  public boolean checkSession(int functionId) {
    // TODO  检查session!
    //    if (sessionContext == null) this.sessionContext = new SessionContext(this);
    //    return this.sessionContext.checkSession(functionId);
    return true;
  }

  /**
   * 设置返回结果
   *
   * @param retCode 返回结果Code
   * @param msg 消息
   * @return
   */
  public HttpHandlerContext setResponse(int retCode, String msg) {
    return this.setResponse(retCode, msg, null, null);
  }

  /**
   * 返回结果中增加新的节点
   *
   * @param value 节点值
   */
  public HttpHandlerContext setResponseExData(Object value) {
    this.response.assignExData(value);
    return this;
  }

  /**
   * 设置返回的数据对象
   *
   * @param value 对象
   * @return 返回自己
   */
  public HttpHandlerContext setResponseData(Object value) {
    this.response.assignData(value);
    return this;
  }

  /**
   * 设置返回结果
   *
   * @param retCode 返回结果Code
   * @param msg 消息
   * @param data 数据
   * @param exData 附加数据
   * @return
   */
  public HttpHandlerContext setResponse(int retCode, String msg, Object data, Object exData) {
    this.response.addResult(retCode, msg);
    this.response.assignData(data);
    this.response.assignExData(exData);
    return this;
  }

  public HttpHandlerContext setFatalError(String msg, Object... args) {
    this.response.addResult(HttpResponse.FatalError, String.format(msg, args));
    return this;
  }

  public HttpHandlerContext setFatalError(String msg) {
    this.response.addResult(HttpResponse.FatalError, msg);
    return this;
  }

  public HttpHandlerContext setError(String msg) {
    this.response.addResult(HttpResponse.ErrorWithMsg, msg);
    return this;
  }

  public HttpHandlerContext setError(String msg, Object... args) {
    this.response.addResult(HttpResponse.ErrorWithMsg, String.format(msg, args));
    return this;
  }

  /**
   * 取得通过post 和参数 传入的参数键值对
   *
   * @return 参数值
   */
  public Map<String, String> getParamMap() {
    Map<String, String> condition = this.requestContext.getParamMap();
    if (condition != null && condition.get("serverId") != null) {
      if (condition.get("serverId").equals("")) {
        condition.put("serverId", this.getServeIds());
      }
    }
    return condition;
  }

  /**
   * 得到当前登录人的 serverIds
   *
   * @return
   */
  public String getServeIds() {
    //    String serverIds_ = null;
    //    SysSession session = this.getSession();
    //    switch (session.getSysUserType_Value()) {
    //      case SysUser:
    //        SysUser user = (SysUser) session.getAttribute(Constants.CURRENT_USER);
    //        if (user != null) {
    //          serverIds_ = user.getServerIds();
    //        }
    //        break;
    //      case CmUser:
    //        //CmUser cmUser = (CmUser) session.getAttribute(Constants.CURRENT_USER);
    //			/*if (cmUser != null) {
    //				serverIds_ = String.valueOf(cmUser.getServerId());
    //			}*/
    //        break;
    //      default:
    //        serverIds_ = this.getPostStringParam(SysServer.M.serverId);
    //        break;
    //    }
    //    // else {TODO 应该拦截sysuser没有的}
    //    return serverIds_;

    return "serverId";
  }

  /**
   * 根据当前传入的请求计算web处理器的名字
   *
   * @return
   */
  public String getHandlerName() {
    return this.requestContext.getHandlerName();
  }

  /**
   * 检查通过网址get传入的参数是否存在
   *
   * @param paramName 参数名
   * @return
   */
  public boolean hasParam(String paramName) {
    return this.requestContext.hasParam(paramName);
  }

  /**
   * 读取通过网址get传入的参数值
   *
   * <p>ex: 这个网址 /m/sys/common/sendPhoneCode?phoneNo=13891970427&type=InviteFriend
   *
   * <p>String tel = ctx.getParam("phoneNo"); // = 13891970427
   *
   * <p>String tel = ctx.getParam("aaa"); // 报错
   *
   * @param paramName 参数名
   * @return
   */
  public String getParam(String paramName) {
    return this.requestContext.getParam(paramName);
  }

  public Integer getIntegerParam(String param) {
    return Integer.valueOf(this.getParam(param));
  }

  public Long getLongParam(String param) {
    return Long.valueOf(this.getParam(param));
  }

  public Double getDoubleParam(String param) {
    return Double.valueOf(this.getParam(param));
  }

  public Boolean getBooleanParam(String param) {
    return Boolean.valueOf(this.getParam(param));
  }

  public String getParam(String paramName, String defaultValue) {
    return this.requestContext.getParam(paramName, defaultValue);
  }

  public Integer getIntegerParam(String paramName, Integer defaultValue) {
    String result = this.getParam(paramName, null);
    if (StringUtils.isBlank(result)) return defaultValue;
    else return Integer.valueOf(result);
  }

  public Map<String, String> getFormData() {
    return this.requestContext.getFormData();
  }

  /**
   * 获取ip地址
   *
   * @return
   */
  public String getRemoteIp() {
    return this.requestContext.getRemoteIp();
  }
  /** 将数据写会客户端 */
  public void writeToChannel() {
    writeToChannel(this.response.toString());
  }

  void beginLogger(Class<?> clazz, Method method) {
    log.debug(
        String.format(
            "START: remote= %s url= %s method %s.%s",
            this.getRemoteIp(), this.getUri(), clazz.getName(), method.getName()));
    if (this.getRequestBody() != null && !this.getRequestBody().equals(""))
      /*
       * if (this.getRequestBody().length() > 300) LogUtil.debug(String.format("BODY:%s...",
       * this.getRequestBody().subSequence(0, 300))); else
       */
      log.debug(String.format("BODY:%s", this.getRequestBody()));
  }

  public void reportError(String msg) {
    log.error(
        String.format(
            "%s: remote= %s url= %s", msg, this.requestContext.getRemoteIp(), this.getUri()));
    if (this.getRequestBody() != null && !this.getRequestBody().equals(""))
      // if (this.getRequestBody().length() > 300)
      // LogUtil.error(String.format("BODY:%s...", this.getRequestBody().subSequence(0, 300)));
      // else
      log.error(String.format("BODY:%s", this.getRequestBody()));
  }

  /**
   * 返回通过post传回的JSON 字符串
   *
   * @return
   */
  public String getRequestBody() {
    return this.requestContext.getRequestBody();
  }

  /**
   * 返回通过post传回的JSON对象
   *
   * @return
   */
  public JSONObject getRequestJson() {
    return this.requestContext.getPostJson();
  }

  /**
   * 检查post参数中是否包含指定名称的参数
   *
   * @author luke
   */
  public boolean hasPostParam(String paramName) {
    return this.requestContext.hasPostParam(paramName);
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
  public Object getPostParamByNames(String... names) {
    return this.requestContext.getPostParamByNames(names);
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
  public Object getPostParamStringByNames(String... names) {
    return this.requestContext.getPostParamStringByNames(names);
  }

  /**
   * 获取排序对象
   *
   * @param defaultOrder 默认排序字段
   * @param defaultOrderItemType 默认排序方式
   * @return
   */
  public List<SqlOrderByItem> getOrderBy(
      String defaultOrder, SqlOrderItemType defaultOrderItemType) {
    return this.requestContext.getOrderBy(defaultOrder, defaultOrderItemType);
  }

  /**
   * 获取排序对象
   *
   * @param defaultOrder 默认排序字段
   * @param defaultOrderItemType 默认排序方式
   * @return
   */
  public SqlOrderBy getOrderBy0(String defaultOrder, SqlOrderItemType defaultOrderItemType) {
    SqlOrderBy orderBy = new SqlOrderBy();
    orderBy.addAll(this.getOrderBy(defaultOrder, defaultOrderItemType));
    return orderBy;
  }

  /**
   * 获取分页对象
   *
   * @return
   */
  public Pager getPager() {
    return this.requestContext.getPager();
  }

  /**
   * 获取分页对象
   *
   * @return
   */
  public Pager getPostPager() {
    return this.requestContext.getPostPager();
  }

  /**
   * 取得通过post body传入的参数，如果没有传入，报错
   *
   * @param paramName 参数名
   * @return 参数值
   */
  public Object getPostParam(String paramName) {
    return this.requestContext.getPostParam(paramName);
  }

  /**
   * 取得通过post body传入的参数，如果没有传入，报错
   *
   * @param paramName 参数名
   * @return 参数值
   */
  public Object getPostParam(String paramName, Object defaultValue) {
    return this.requestContext.getPostParam(paramName, defaultValue);
  }

  public Long getPostParamLong(String paramName, Long defaultValue) {
    Object result = this.requestContext.getPostParam(paramName, defaultValue);
    if (result == null || result.toString() == "") return null;
    else return Long.valueOf(result.toString());
  }

  /**
   * 取得通过post body传入的参数，如果没有传入，报错
   *
   * @param paramName 参数名
   * @return 参数值
   */
  public String getPostStringParam(String paramName) {
    return this.requestContext.getPostStringParam(paramName, null);
  }

  /**
   * 取得通过post body传入的参数
   *
   * @param paramName 参数名
   * @param defaultValue 缺省值
   * @return 参数值
   */
  public String getPostStringParam(String paramName, String defaultValue) {
    return this.requestContext.getPostStringParam(paramName, defaultValue);
  }

  /**
   * 取得通过post body传入的参数，如果没有传入，报错
   *
   * @param paramName 参数名
   * @return 参数值
   */
  public int getPostIntParam(String paramName) {
    return this.getPostIntParam(paramName, -1);
  }

  /**
   * 取得通过post body传入的参数
   *
   * @param paramName 参数名
   * @param defaultValue 缺省值
   * @return 参数值
   */
  public int getPostIntParam(String paramName, int defaultValue) {
    String result = this.requestContext.getPostStringParam(paramName, defaultValue + "");
    int s = -1;
    try {
      s = Integer.valueOf(result);
    } catch (Exception e) {
      s = defaultValue;
    }
    return s;
  }

  public FullHttpRequest getRequest() {
    return this.request;
  }

  /**
   * 取得本次连接的ChannelContext
   *
   * @return
   */
  public ChannelHandlerContext getChannelContext() {
    return this.channelContext;
  }

  private static void orderByListOfContent(JSONObject o, QueryContext cond) {
    if (o != null) {
      JSONArray arr = o.getJSONArray("orderBy");
      if (arr != null)
        for (Object order : arr) {
          SqlOrderByItem o1;
          if (order instanceof String) {
            o1 = new SqlOrderByItem(order.toString(), SqlOrderItemType.ASC);
            cond.getOrderBy().add(o1);
          } else if (order instanceof JSONObject) {
            JSONObject order2 = (JSONObject) order;
            o1 =
                new SqlOrderByItem(
                    order2.get("field").toString(),
                    SqlOrderItemType.valueOf(order2.get("type").toString()));
            cond.getOrderBy().add(o1);
          } else throw new ISRuntimeException("错误的orderBy格式");
        }
    }
  }

  public void buildQueryContext(QueryContext cond) {
    String pageindex = this.getPostStringParam(Page_Index_ParamName);
    String pagesize = this.getPostStringParam(Page_Size_ParamName);
    cond.PAGE(pageindex, pagesize);
    orderByListOfContent(this.getRequestJson(), cond);
  }

  @Override
  public String toString() {
    return String.format(
        "url=%s\nbody=%s\nresponse=%s",
        this.getUri(), this.getRequestBody(), this.response.toString());
  }
  /**
   * 读取参数中传入的文件内容 注意必须先调用 processUploadFiles
   *
   * @param name 参数名
   * @return 文件内容
   */
  public byte[] getFileContent(String name) {
    return this.requestContext.getFileContent(name);
  }

  /**
   * 读取调用的网址
   *
   * @return
   */
  public String getReferUrl() {
    return this.request.headers().get(HttpHeaderNames.REFERER);
  }

  public void checkRuntime() {
    if (AppConstant.IS_RUNTIME) throw new ISRuntimeException("运行时测试代码不能调用(url=%s)", this.getUri());
  }

  /**
   * 将一个post参数转换为对象
   *
   * <p>ex: CmUser cmUser = (CmUser) ctx.bodyToObject(CmUser.class);
   *
   * @param clazz 对象类型
   * @return
   */
  public <T> T bodyToObject(Class<T> clazz) {
    return (T) JSONUtil.parseObject(getRequestBody(), clazz);
  }

  public HttpHandlerContext writeBuffToClient(byte[] buff, String fileName) {
    responseCount++;
    NettyTool.writeByteToClient(
        buff, fileName, this.channelContext.channel(), this.request, this.getOrigin());
    return this;
  }

  public HttpHandlerContext writeXmlToClient(String text, String fileName) {
    responseCount++;
    NettyTool.writeXmlToClient(text, this.channelContext.channel(), this.request);
    return this;
  }

  public void writeHtml(String html) {
    responseCount++;
    NettyTool.writeHtmlToClient(html, this.channelContext.channel(), this.request);
  }

  public <T> T getCondition(Class<T> clazz) {
    return getCondition_(clazz, getRequestBody());
  }

  public <T> T getConditionGet(Class<T> clazz) {
    Map<String, String> condition = this.requestContext.getParamMap();
    // json 对象转string
    String json = new Gson().toJson(condition);
    // JackSonUtil.bean2Json(condition);
    return getCondition_(clazz, json);
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

  private <T> T getCondition_(Class<T> clazz, String json) {
    T t = (T) JSONUtil.parseObject(json, clazz);
    if (t == null) {
      return null;
    }
    try {
      Class<?> tCls = t.getClass().getSuperclass();
      Field[] fields = tCls.getDeclaredFields();
      for (Field field : fields) {
        // TODO serverid 处理
        if (field.getName().equals("serverId")
            && field.getType().getName().equals("java.lang.String")) {
          Method method = tCls.getDeclaredMethod("getServerId"); // gettter
          Object rtnObj = method.invoke(t);
          String serverId = null;
          if (rtnObj != null) {
            serverId = rtnObj.toString();
          }
          String serverId_g = this.getServeIds();
          if (StringUtils.isBlank(serverId)) { // 请求没有serverId
            if (StringUtils.isBlank(serverId_g)) { // 用户没有任何城市权限
              serverId = "999";
            } else { // 请求没有，按用户实际拥有的城市查询
              serverId = serverId_g;
            }
          } else { // 如果传递了，则用传递的
            if (StringUtils.isBlank(serverId_g)) { // 用户没有城市权限，但是请求中有，认为是攻击
              serverId = "999";
            }
          }
          method = tCls.getDeclaredMethod("setServerId", String.class); // setter
          method.invoke(t, serverId); // 调用 set方法设置值
          break;
        }
      }
    } catch (Exception e) {
      log.error("设置serverId时报错，" + e.getMessage(), e);
    }
    return t;
  }

  /**
   * 不区分城市的条件对象
   *
   * @param clazz
   * @return
   */
  public <T> T getCondition0(Class<T> clazz) {
    return (T) JSONUtil.parseObject(getRequestBody(), clazz);
  }
}
