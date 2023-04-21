package com.freelycar.voice.util;

import android.util.Log;

import com.freelycar.voice.service.FreeVoiceService;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SafeConnection {
	private static final String TAG = FreeVoiceService.class.getSimpleName();

	private final static int TIME_WAIVER_MS = 1000 * 600;
	private final static String SECURITY_KEY = "zkjthVigevWLgz7tVOLAcIaeM30ypCuwHimSFZU96c2zkNnCRkVfCd4OFWWxCOQ3nLG9rU1HnXX1k8LDSnNxyeGKORxVzfP5yjaXDQC6yFeI31FPjBha089lDvu3F9PR";
	// Modify according to your needs
	private final static OkHttpClient mClient = new OkHttpClient().newBuilder().retryOnConnectionFailure(true)
			.connectTimeout(60, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS)
			.build();

	public static class DataWithDigest {
		long timestamp;
		String jsondata;
		String digest;
		String nonce;

		public DataWithDigest() {

		}

		public DataWithDigest(String jsondata, long timestamp, String nonce, String digest) {
			this.timestamp = timestamp;
			this.jsondata = jsondata;
			this.digest = digest;
			this.nonce = nonce;
		}

		public FormBody.Builder toFormBuilder() {
			FormBody.Builder builder = new FormBody.Builder();
			builder.add("timestamp", String.valueOf(timestamp));
			builder.add("jsondata", jsondata);
			builder.add("digest", digest);
			builder.add("nonce", nonce);
			return builder;
		}
	}

	private static boolean verifyDataWithDigest(DataWithDigest d) {
		String digest = ShaUtil.toSHA1(d.timestamp + d.jsondata + SECURITY_KEY + d.nonce);
		MyLogUtils.file(TAG,"verifyDataWithDigest:  "+ digest+"  -  "+d.digest+"  -  "+d.nonce+"  -  "+d.timestamp+  "  -  ");
		return digest != null && digest.equals(d.digest) && d.timestamp > System.currentTimeMillis() - TIME_WAIVER_MS;

	}

	private static <T> T getOrignalObject(DataWithDigest dataWithDigest, Class<T> classOfT) throws IOException {
		if (verifyDataWithDigest(dataWithDigest)) {
			Gson gson = new Gson();
			//logger.info(dataWithDigest.jsondata);
			MyLogUtils.file(TAG,"getOrignalObject:  "+ dataWithDigest.jsondata);
			return gson.fromJson(dataWithDigest.jsondata, classOfT);
		} else {
			//logger.error("Safe connection verify failed ");
			throw new IOException("SafeConnection verify failed!");
		}
	}

	public static DataWithDigest encoderForObject(Object obj) {
		long timestamp = System.currentTimeMillis();
		String nonce = getRandomString(32);
		Gson gson = new Gson();
		String jsonData = gson.toJson(obj);
		//logger.info(jsonData);
		String digest = ShaUtil.toSHA1(timestamp + jsonData + SECURITY_KEY + nonce);
		MyLogUtils.file(TAG,"encoderForObject:  "+ digest+"  -  "+jsonData+"  -  "+nonce+"  -  "+timestamp);
		return new DataWithDigest(jsonData, timestamp, nonce, digest);
	}

	public static <T> T decodeFromMap(Map<String, String> map, Class<T> classOfT) throws IOException {
		DataWithDigest dataWithDigest = new DataWithDigest(map.get("jsondata"), Long.valueOf(map.get("timestamp")),
				map.get("nonce"), map.get("digest"));
		return getOrignalObject(dataWithDigest, classOfT);
	}

	public static <T> T decodeFromString(String s, Class<T> classOfT) throws IOException {
		Gson gson = new Gson();
		DataWithDigest dataWithDigest = gson.fromJson(s, DataWithDigest.class);
		return getOrignalObject(dataWithDigest, classOfT);

	}

	public static String getRandomString(int length) {
		String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuffer sb = new StringBuffer();
		int len = KeyString.length();
		for (int i = 0; i < length; i++) {
			sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
		}
		return sb.toString();
	}

	public static <T> T postAndGetResponse(String url, Object in, Class<T> classOfT) throws IOException {

		try {

			// SafeConnection::: Generate the signed data
			final DataWithDigest data = SafeConnection.encoderForObject(in);
			final FormBody.Builder builder = data.toFormBuilder();
			final Request request = new Request.Builder().url(url).post(builder.build()).build();
			final Response response = mClient.newCall(request).execute();
			MyLogUtils.file(TAG,"postAndGetResponse:  "+ request.url()+"  -  "+request.headers()+"  -  "+request.method()+"  -  "+response.isSuccessful()+"  - ");
			if (response.isSuccessful()) {
				String x = response.body().string();
				return SafeConnection.decodeFromString(x, classOfT);
			} else {
				//logger.error("Connection error!");
				throw new IOException("Connection error!");
			}
		} catch (IOException e) {
			throw e;
		}
	}

}
