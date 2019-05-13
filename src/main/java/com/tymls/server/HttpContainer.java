package com.tymls.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentMap;

@Sharable
@Slf4j
@Data
public class HttpContainer {
  /** 监听端口 */
  private int port;

  /** http服务对外提供的所有处理器 */
  private ConcurrentMap<String, HttpHandler> handlers;

  public void start() {
    new Thread(new RunThread()).start();
  }

  public class RunThread implements Runnable {
    @Override
    public void run() {
      log.info("netty服务启动端口 : {}", port);
      EventLoopGroup bossGroup = new NioEventLoopGroup();
      EventLoopGroup workerGroup = new NioEventLoopGroup();
      Initializer initializer = new Initializer();
      try {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 128)
            // 通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
            .option(ChannelOption.TCP_NODELAY, true)
            // .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(402800))
            .childHandler(initializer);
        ChannelFuture f = b.bind(port).sync();
        f.channel().closeFuture().sync();
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      } finally {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
      }
    }
  }

  public class Initializer extends ChannelInitializer<SocketChannel> {
    private DispatcherHandler disptacherHandler = new DispatcherHandler(getHandlers());

    /*
     * 当新连接accept的时候，这个方法会调用
     *
     * @param ch
     */
    @Override
    public void initChannel(SocketChannel ch) {
      ChannelPipeline p = ch.pipeline();
      p.addLast(new HttpRequestDecoder());
      p.addLast(new HttpResponseEncoder());
      // TODO 内容设置为Integer 的最大值
      p.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
      // 参考这篇文章 http://blog.csdn.net/busbanana/article/details/48002853
      p.addLast(new ChunkedWriteHandler());
      p.addLast(new HttpContentCompressor());
      p.addLast(disptacherHandler);
    }
  }
}
