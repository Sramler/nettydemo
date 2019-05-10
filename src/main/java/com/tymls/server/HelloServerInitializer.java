package com.tymls.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 描述:初始化器,channel注册后,会执行里面的相应的初始化方法
 *
 * @author elinkme
 */
public class HelloServerInitializer extends ChannelInitializer<SocketChannel> {
  protected void initChannel(SocketChannel socketChannel) throws Exception {

    // 通过socketChannel去获取对应的管道
    ChannelPipeline channelPipeline = socketChannel.pipeline();

    // 通过管道,添加handler
    // httpServerCodec是由netty自己提供的助手类,可以理解为拦截器

    // 当请求到服务端,我们需要做解码,响应到客户端做编码
    // websocket 基于http协议,所以要有http编解码器
    channelPipeline.addLast(new HttpServerCodec());

    // 对写大数据流的支持
    channelPipeline.addLast(new ChunkedWriteHandler());

    // 对httpMessage进行聚合,聚合成FullHttpResponse
    // 几乎在netty中的编程,都会使用到此hanler
    channelPipeline.addLast(new HttpObjectAggregator(1024 * 64));
    // =========== 以上是用于支持http协议 ============

    // channelPipeline.addLast(new DispatcherHandler());
  }
}
