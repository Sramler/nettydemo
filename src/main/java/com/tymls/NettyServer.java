package com.tymls;

import com.tymls.server.HelloServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log
@SpringBootApplication
public class NettyServer {

  public static void main(String[] args) {
    SpringApplication.run(NettyServer.class, args);

    // v1
    //    try {
    //      run(args);
    //    } catch (Exception e) {
    //      e.printStackTrace();
    //    }
  }

  public static void run(String[] args) throws Exception {
    log.info("启动netty服务...");

    // 定义一对线程组
    // 主线程组,用于接收客户端连接,但是不做任何处理,跟老板一样,不做事
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    // 从线程组,老板线程组回把任务丢给他,让手下线程组去做任务
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      // netty服务器的创建,ServerBootstrap 是一个启动类
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap
          .group(bossGroup, workerGroup) // 设置主从线程组
          .channel(NioServerSocketChannel.class) // 设置nio的双向通道
          .childHandler(new HelloServerInitializer()); // 字处理器,用于处理workerGroup
      // 启动server,并设置8088为启动的端口好,同时启动方式为同步
      ChannelFuture channelFuture = serverBootstrap.bind(8888);
      log.info("netty服务启动完毕...");
      // 监听关闭的channel,设置同步方式
      channelFuture.channel().closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
