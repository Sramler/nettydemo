package com.tymls.server;

import com.tymls.server.Handler.HelloHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class MyConfig {

  @Bean(name = "httpContainer")
  public HttpContainer httpContainer() {
    HttpContainer httpContainer = new HttpContainer();
    httpContainer.setPort(8888);
    httpContainer.setHandlers(concurrentHashMap());
    httpContainer.start();
    return httpContainer;
  }

  @Bean
  public ConcurrentHashMap concurrentHashMap() {
    ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap<>();
    concurrentHashMap.put("hello/sayHello/", helloHandler());
    return concurrentHashMap;
  }

  @Bean
  public HelloHandler helloHandler() {
    return new HelloHandler();
  }
}
