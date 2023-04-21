package com.freelycar.voice.util;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class DataEncryptInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        RequestBody oldBodyRequest = request.body();

        Buffer requestBuffer = new Buffer();
        oldBodyRequest.writeTo(requestBuffer);
        String oldBodyStr = requestBuffer.readUtf8();
        requestBuffer.close();

        MyLogUtils.file("TAG", "the old body str is :" + oldBodyStr);

        //String randomKeyValue = "hello_" + System.currentTimeMillis() + "_world";
        String randomKeyValue = "zhangsanlisiwangwu";
        String newBodyStr = AESUtils.encrypt(oldBodyStr,randomKeyValue);
        if (TextUtils.isEmpty(newBodyStr)) newBodyStr = "";

        MediaType mediaType = MediaType.parse("text/plain;charset=utf-8");
        RequestBody newRequestBody = RequestBody.create(mediaType, newBodyStr);

        //构建新的request
        Request newRequest = request.newBuilder().header("Content-type", newRequestBody.contentType().toString())
                .header("Content-Length", String.valueOf(newRequestBody.contentLength()))
                .method(request.method(), newRequestBody)
                .header("key", randomKeyValue)
                .build();

        Response response = chain.proceed(newRequest);
        if (response.code() / 200 == 1) {
            ResponseBody oldResponseBody = response.body();

            String oldResponseBodyStr = oldResponseBody.string();

            String newResponseBodyStr = AESUtils.decrypt(oldResponseBodyStr,randomKeyValue);
            if (TextUtils.isEmpty(newResponseBodyStr)) newResponseBodyStr = "data decrypy error";

            ResponseBody responseBody = ResponseBody.create(mediaType, newResponseBodyStr);

            response = response.newBuilder().body(responseBody).build();
        }
        return response;
    }
}


