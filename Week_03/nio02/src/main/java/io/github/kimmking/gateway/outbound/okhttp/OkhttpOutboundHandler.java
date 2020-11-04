package io.github.kimmking.gateway.outbound.okhttp;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.github.kimmking.gateway.outbound.HttpOutboundHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkhttpOutboundHandler implements HttpOutboundHandler {

	private ExecutorService threadPool = new ThreadPoolExecutor(100, 100,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1000));
	
	private OkHttpClient client = new OkHttpClient();
	
	@Override
	public void handle(final ChannelHandlerContext ctx, final FullHttpRequest fullRequest) {
		threadPool.submit(() -> {
			doHandle(ctx, fullRequest);
		});
	}
	
	private void doHandle(ChannelHandlerContext ctx, FullHttpRequest fullRequest) {
		try {
			Request request = new Request.Builder().url(fullRequest.uri()).build();
			Response response = client.newCall(request).execute();
			doHandleResponse(ctx, fullRequest, response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doHandleResponse(ChannelHandlerContext ctx, FullHttpRequest request, Response endpointResponse) {
		FullHttpResponse fullHttpResponse = null;
		try {
			fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(endpointResponse.body().bytes()));
			fullHttpResponse.headers().set("Content-Type", "application/json");
			fullHttpResponse.headers().setInt("Content-Length",
					Integer.parseInt(endpointResponse.header("Content-Length")));
		} catch (Exception e) {
			fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
			ctx.close();
		}
		if (!HttpUtil.isKeepAlive(request)) {
			ctx.write(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
		} else {
			ctx.write(fullHttpResponse);
		}
		ctx.flush();
	}
}
