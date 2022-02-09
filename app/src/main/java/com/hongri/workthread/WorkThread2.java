package com.hongri.workthread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * 两个子线程之间发送消息 -- WorkThread2
 */
public class WorkThread2 extends Thread {

    private static final String TAG = "WorkThread";
    private Handler handler1;

    public WorkThread2() {

    }

    public WorkThread2(Handler handler1) {
        this.handler1 = handler1;
    }

    public static Handler getHandler2() {
        return handler2;
    }

    private static Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "WorkThread2 收到了消息: " + msg.obj);
        }
    };

    @Override
    public void run() {
        super.run();

        Looper.prepare();

        Message msg = new Message();
        msg.obj = "我是 WorkThread2 发送的消息";
        handler1.sendMessage(msg);

        Looper.loop();

        new WorkThread1(handler2).start();

    }
}
