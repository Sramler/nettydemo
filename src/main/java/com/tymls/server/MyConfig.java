package com.tymls.server;

import com.tymls.server.Handler.HelloHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Slf4j
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
    log.info("初始化url映射注入相应的handler");
    ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap<>();
    concurrentHashMap.put("test/hello/", helloHandler());
    return concurrentHashMap;
  }

  @Bean
  public HelloHandler helloHandler() {
    return new HelloHandler();
  }
}
