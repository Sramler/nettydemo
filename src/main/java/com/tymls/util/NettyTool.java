package com.tymls.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
public class NettyTool {

  /**
   * 写出内容到客户端
   *
   * @param content
   * @param channel
   * @throws UnsupportedEncodingException
   */
  public static void writeToClient(String content, Channel channel, FullHttpRequest req) {
    ByteBuf buf = Unpooled.wrappedBuffer(content.getBytes(CharsetUtil.UTF_8));
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buf);
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
    if (channel.isOpen() && channel.isWritable()) {
      log.info("http响应json内容为：{}");
      ChannelFuture future = channel.writeAndFlush(response);
      future.addListener(ChannelFutureListener.CLOSE);
      return;
    }
    response = null;
    errorDeal(req, channel);
  }

  /**
   * 写出内容到客户端
   *
   * @param content
   * @param channel
   * @throws UnsupportedEncodingException
   */
  public static void writeXmlToClient(String content, Channel channel, FullHttpRequest req) {
    ByteBuf buf = Unpooled.wrappedBuffer(content.getBytes(CharsetUtil.UTF_8));
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buf);
    log.info("http响应xml内容为：{}", content);
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/xml; charset=UTF-8");
    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
    if (channel.isOpen() && channel.isWritable()) {
      ChannelFuture future = channel.writeAndFlush(response);
      future.addListener(ChannelFutureListener.CLOSE);
      return;
    }
    response = null;
    errorDeal(req, channel);
  }
  /**
   * 写出tcp对象数据到客户端
   *
   * @param channel
   */
  public static void writeTcpDataToClient(Object obj, Channel channel) {
    log.info("tcp响应内容为：" + obj);
    channel.writeAndFlush(obj);
  }
  /**
   * 写出socket内容到客户端
   *
   * @param content
   * @param channel
   */
  public static void writeSocketFrameToClient(String content, Channel channel) {
    channel.writeAndFlush(new TextWebSocketFrame(content));
  }
  /**
   * 写出html内容到客户端
   *
   * @param content
   * @param channel
   */
  public static void writeHtmlToClient(String content, Channel channel, FullHttpRequest req) {
    ByteBuf buf = Unpooled.wrappedBuffer(content.getBytes(CharsetUtil.UTF_8));
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buf);
    log.info("http响应html内容为：{}", content);
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
    if (channel.isOpen() && channel.isWritable()) {
      ChannelFuture future = channel.writeAndFlush(response);
      future.addListener(ChannelFutureListener.CLOSE);
      return;
    }
    response = null;
    errorDeal(req, channel);
  }
  /**
   * 写出图片流到客户端
   *
   * @param buff 图片二进制数组
   * @param channel
   * @param req
   */
  public static void writeByteToClient(
      byte[] buff, String fileName, Channel channel, FullHttpRequest req) {
    ByteBuf buf = Unpooled.wrappedBuffer(buff);
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buf);
    response
        .headers()
        .set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream"); // 告诉浏览器传递的是文件流
    response
        .headers()
        .set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes()); // 文件大小
    response
        .headers()
        .set(
            HttpHeaderNames.CONTENT_DISPOSITION,
            "attachment; filename*=utf-8''" + fileName); // 指定文件名
    response.headers().set(HttpHeaderNames.PRAGMA, "No-cache");
    response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");
    response.headers().set(HttpHeaderNames.EXPIRES, 0);
    if (channel.isOpen() && channel.isWritable()) {
      log.info("http响应图片流，长度=" + buff.length);
      ChannelFuture future = channel.writeAndFlush(response);
      future.addListener(ChannelFutureListener.CLOSE);
      return;
    }
    response = null;
    errorDeal(req, channel);
  }
  /**
   * 异常情况判断
   *
   * @param req
   * @param channel
   */
  private static void errorDeal(FullHttpRequest req, Channel channel) {
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
    if ((req != null && !HttpUtil.isKeepAlive(req)) || response.status().code() != 200) {
      log.info("Generate an error page if response getStatus code is not OK (200).");
      ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
      response.content().writeBytes(buf);
      HttpUtil.setContentLength(response, response.content().readableBytes());
      buf.release();
      ChannelFuture future = channel.writeAndFlush(response);
      future.addListener(ChannelFutureListener.CLOSE);
    }
  }

  public static void writeByteToClient(
      byte[] buff, String fileName, Channel channel, FullHttpRequest req, String origin) {
    ByteBuf buf = Unpooled.wrappedBuffer(buff);
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buf);
    response
        .headers()
        .set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream"); // 告诉浏览器传递的是文件流
    // header("Accept-Length: 2048");                             //文件大小
    response
        .headers()
        .set(
            HttpHeaderNames.CONTENT_DISPOSITION,
            "attachment; filename*=utf-8''" + fileName); // 指定文件名
    if (origin != null) {
      response
          .headers()
          .set(
              HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS,
              "content-disposition"); // 跨域请求设置此字段将改名称包含的header暴漏给浏览器端
      response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin); // 指定文件名
      response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
    }
    response.headers().set(HttpHeaderNames.PRAGMA, "No-cache");
    response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");
    response.headers().set(HttpHeaderNames.EXPIRES, 0);
    if (channel.isOpen() && channel.isWritable()) {
      log.info("http响应图片流，长度={}", buff.length);
      ChannelFuture future = channel.writeAndFlush(response);
      future.addListener(ChannelFutureListener.CLOSE);
      return;
    }
    response = null;
    errorDeal(req, channel);
  }
}
