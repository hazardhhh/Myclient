package com.example.client.common.http;

import android.content.Context;
import android.os.Handler;

import com.example.client.common.framework.UcTaskExecutor;

import java.util.Map;

/**
 * Created by ucstar on 2015/3/6.
 */
public class UcstarHttpClient {

    /**
     * *********************** Http Task & Callback *************************
     */
    public interface UcstarHttpCallback {
        void onResponse(String response, int code, Throwable e);
    }

    public class UcHttpTask implements Runnable {

        private String url;
        private Map<String, String> headers;
        private String jsonBody;
        private UcstarHttpCallback callback;
        private boolean post;

        public UcHttpTask(String url, Map<String, String> headers, String jsonBody, UcstarHttpCallback callback) {
            this(url, headers, jsonBody, callback, true);
        }

        public UcHttpTask(String url, Map<String, String> headers, String jsonBody, UcstarHttpCallback callback, boolean post) {
            this.url = url;
            this.headers = headers;
            this.jsonBody = jsonBody;
            this.callback = callback;
            this.post = post;
        }

        @Override
        public void run() {
            final HttpClientWrapper.HttpResult<String> result = post ?
                    HttpClientWrapper.post(url, headers, jsonBody) : HttpClientWrapper.get(url, headers);

            // do callback on ui thread
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onResponse(result.obj, result.code, result.e);
                    }
                }
            });
        }
    }

    /**
     * ************************ Single instance **************************
     */
    private static UcstarHttpClient instance;

    public synchronized static UcstarHttpClient getInstance() {
        if (instance == null) {
            instance = new UcstarHttpClient();
        }

        return instance;
    }

    private UcstarHttpClient() {

    }

    /**
     * **************** Http Config & Thread pool & Http Client ******************
     */

    private boolean inited = false;

    private UcTaskExecutor executor;

    private Handler uiHandler;

    public void init(Context context) {
        if (inited) {
            return;
        }

        // init thread pool
        executor = new UcTaskExecutor("NIM_HTTP_TASK_EXECUTOR", new UcTaskExecutor.Config(1, 3, 10 * 1000, true));
        uiHandler = new Handler(context.getMainLooper());
        inited = true;
    }

    public void release() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    public void execute(String url, Map<String, String> headers, String body, UcstarHttpCallback callback) {
        execute(url, headers, body, true, callback);
    }

    public void execute(String url, Map<String, String> headers, String body, boolean post, UcstarHttpCallback callback) {
        if (!inited) {
            return;
        }

        executor.execute(new UcHttpTask(url, headers, body, callback, post));
    }
}
