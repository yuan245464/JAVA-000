package io.github.kimmking.gateway.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface HttpOutboundHandler {

	void handle(ChannelHandlerContext ctx, FullHttpRequest fullRequest);
	
}
