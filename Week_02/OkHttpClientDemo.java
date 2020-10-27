package hy.com.hytestproject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpClientDemo {
	public static void main(String[] args) throws IOException {
		OkHttpClient client = new OkHttpClient();
		String url = "http://localhost:8088/api/hello";
		try {
			Request request = new Request.Builder().url(url).build();
			Response response = client.newCall(request).execute();
			System.out.println(response.body().string());
		} finally {
			client.connectionPool().evictAll();
		}
	}
}
