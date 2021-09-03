package com.example.client.common.framework;

import android.os.Handler;

import com.example.client.utils.UcstarUIKit;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by ucstar on 2015/3/12.
 */
public class UcSingleThreadExecutor {

    private static com.example.client.common.framework.UcSingleThreadExecutor instance;

    private Handler uiHander;
    private Executor executor;

    private UcSingleThreadExecutor() {
        uiHander = new Handler(UcstarUIKit.getContext().getMainLooper());
        executor = Executors.newSingleThreadExecutor();
    }

    public synchronized static com.example.client.common.framework.UcSingleThreadExecutor getInstance() {
        if (instance == null) {
            instance = new com.example.client.common.framework.UcSingleThreadExecutor();
        }

        return instance;
    }

    public <T> void execute(UcstarTask<T> task) {
        if (executor != null) {
            executor.execute(new UcstarRunnable<>(task));
        }
    }

    public void execute(Runnable runnable) {
        if (executor != null) {
            executor.execute(runnable);
        }
    }

    /**
     * ****************** model *************************
     */

    public interface UcstarTask<T> {
        T runInBackground();

        void onCompleted(T result);
    }

    private class UcstarRunnable<T> implements Runnable {

        public UcstarRunnable(UcstarTask<T> task) {
            this.task = task;
        }

        private UcstarTask<T> task;

        @Override
        public void run() {
            final T res = task.runInBackground();
            if (uiHander != null) {
                uiHander.post(new Runnable() {
                    @Override
                    public void run() {
                        task.onCompleted(res);
                    }
                });
            }
        }
    }
}
