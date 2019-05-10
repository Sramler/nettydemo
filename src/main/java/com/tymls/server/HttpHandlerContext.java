package com.tymls.server;

import com.tymls.server.model.HttpResponse;
import com.tymls.server.vo.AppConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Log
public class HttpHandlerContext {

  public static String BASEURL = "/m/";

  protected transient ChannelHandlerContext channelContext;
  @Getter private FullHttpRequest request;
  private HttpResponse response;

  private transient RequestContext requestContext;

  @Getter private int responseCount = 0;

  private transient long startRunAt;
  private transient FullHttpResponse httpResponse;

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

  /** 将数据写会客户端 */
  public void writeToChannel() {
    writeToChannel(this.response.toString());
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
}
