package com.tymls.server.Handler;

import com.tymls.server.AbstractHttpHandler;
import com.tymls.server.HttpHandlerContext;
import com.tymls.server.model.Handler;
import com.tymls.server.model.HandlerMethod;

@Handler(uri = "/hello/")
public class HelloHandler extends AbstractHttpHandler {

  @HandlerMethod(uri = "sayhello")
  public HttpHandlerContext sayHello(HttpHandlerContext ctx) {
    return ctx.setResponseData("hello");
  }
}
