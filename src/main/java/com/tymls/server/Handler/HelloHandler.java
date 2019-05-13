package com.tymls.server.Handler;

import com.tymls.server.AbstractHttpHandler;
import com.tymls.server.HttpHandlerContext;
import com.tymls.server.model.Handler;
import com.tymls.server.model.HandlerMethod;
import lombok.extern.slf4j.Slf4j;

@Handler(uri = "hello/")
@Slf4j
public class HelloHandler extends AbstractHttpHandler {

  @HandlerMethod(uri = "sayhello")
  public HttpHandlerContext sayHello(HttpHandlerContext ctx) {
    String a = ctx.getPostStringParam("hello");
    Integer b = ctx.getPostIntParam("keyword");
    log.info("传入参数为:{}{}", a, b);
    return ctx.setResponseData("hello!!!");
  }

  @HandlerMethod(uri = "test")
  public HttpHandlerContext tellHello(HttpHandlerContext ctx) {
    return ctx.setResponseData("这是一个测试!!!");
  }
}
