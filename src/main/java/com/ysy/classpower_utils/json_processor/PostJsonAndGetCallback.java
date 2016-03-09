package com.ysy.classpower_utils.json_processor;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

/**
 * Created by 姚圣禹 on 2016/2/14.
 */
public class PostJsonAndGetCallback {

    private static final String CONTENT_TYPE_JSON = "application/json";

    public PostJsonAndGetCallback(AsyncHttpClient client, Context context, String url, String json, ResponseHandlerInterface responseHandler) {
        try {
            client.post(context, url, new StringEntity(json, "UTF-8"), CONTENT_TYPE_JSON, responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
