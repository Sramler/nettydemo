package com.tymls.server;

import com.tymls.server.common.ConfigConstants;
import com.tymls.server.common.auth.CommonUtil;
import com.tymls.server.model.Handler;
import com.tymls.server.model.HttpResponse;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Sharable
public class DispatcherHandler extends SimpleChannelInboundHandler<Object> {

  /** http服务对外提供的所有处理器 */
  private ConcurrentMap<String, HttpHandler> handlers;

  private ConcurrentMap<String, HandlerDefine> handlerDefins;

  public DispatcherHandler(ConcurrentMap<String, HttpHandler> handlers) {
    this.handlers = handlers;
  }

  protected static class HandlerDefine {
    public HttpHandler handler;
    public Handler annotion;

    public HandlerDefine(HttpHandler handler) {
      this.handler = handler;
      this.annotion = handler.getClass().getAnnotation(Handler.class);
    }

    public String geturi() {
      if (annotion != null && annotion.uri() != null) return annotion.uri();
      else return this.handler.getClass().getName();
    }
  }

  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof FullHttpRequest) { // 如果是HTTP请求，进行HTTP操作
      FullHttpRequest req = (FullHttpRequest) msg;
      HttpHandlerContext context = new HttpHandlerContext(ctx, req);
      HandlerDefine handlerDefine = null;
      // V1
      // handleHttpRequest(ctx, (FullHttpRequest) msg);
      try {
        String uri = req.uri();
        if (uri.contains("?")) {
          uri = uri.substring(0, uri.indexOf("?"));
        }
        if (uri.contains("/favicon.ico")) {
          return;
        }
        if (!ConfigConstants.NOT_AUTH_URIS.contains(uri)) { // 签名认证
          Map<String, String> param = context.getParamMap();
          if (!(param != null && param.get("noSign") != null)) { // postMan调试
            if (!authentication(param)) {
              String noSign = "非法访问!";
              context.writeToChannel(HttpResponse.getResultJson(-1, noSign));
              context.setError(noSign);
              // TODO 错误日志
              // context.addSysLog(false); // 记录系统日志
              return;
            }
          }
        }
        handlerDefine = this.getHandlerDefine(context);
        handlerDefine.handler.doHandle(context);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        context.setFatalError(e.getMessage());
        context.writeToChannel();
        // context.addSysLog(true); // 记录系统日志
        return;
      }
    } else {
      log.info("暂不支持该解析!!!");
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }

  /**
   * 鉴权
   *
   * @param map
   * @return
   */
  private boolean authentication(Map<String, String> map) {
    String signature = map.get("signature");
    String appsecret = ConfigConstants.APP_SECRET;
    map.remove("signature");
    String signature2 = CommonUtil.signatureWithParamsOnly(map, appsecret);
    if (StringUtils.isNotBlank(signature)
        && StringUtils.isNotBlank(signature2)
        && signature.equals(signature2)) {
      return true;
    }
    return false;
  }

  public HandlerDefine getHandlerDefine(HttpHandlerContext ctx) {
    if (this.handlerDefins == null) {
      this.buildHandlerDefines();
    }
    String handlerKey = ctx.getHandlerName();
    HandlerDefine result = handlerDefins.get(handlerKey);
    if (result == null) {
      throw new ISRuntimeException("处理器 %s 没有注册，网址%s 错误", handlerKey, ctx.getUri());
    }
    return result;
  }

  private void buildHandlerDefines() {
    handlerDefins = new ConcurrentHashMap<String, HandlerDefine>();
    for (Map.Entry<String, HttpHandler> item : handlers.entrySet()) {
      handlerDefins.putIfAbsent(item.getKey(), new HandlerDefine(item.getValue()));
    }
  }
}
