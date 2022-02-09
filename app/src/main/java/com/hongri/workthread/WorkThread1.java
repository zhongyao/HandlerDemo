package com.hongri.workthread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * 两个子线程之间发送消息 -- WorkThread1
 */
public class WorkThread1 extends Thread {

    private static final String TAG = "WorkThread";
    private Handler handler2;

    public WorkThread1(Handler handler2) {
        this.handler2 = handler2;
    }

    public static Handler getHandler1() {
        return handler1;
    }

    private static Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "WorkThread1 收到了消息: " + msg.obj);
        }
    };

    @Override
    public void run() {
        super.run();

        Looper.prepare();

        Message msg = new Message();
        msg.obj = "我是 WorkThread1 发送的消息";
        handler2.sendMessage(msg);

        Looper.loop();

        new WorkThread2(handler1).start();
    }
}
