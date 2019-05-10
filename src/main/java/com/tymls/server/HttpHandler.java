package com.tymls.server;

public interface HttpHandler {
  void doHandle(HttpHandlerContext ctx) throws Exception;
}
