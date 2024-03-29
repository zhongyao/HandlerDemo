package com.hongri.handler;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.hongri.R;
import com.hongri.idlehandler.DelayTaskDispatcher;
import com.hongri.workthread.WorkThread1;
import com.hongri.workthread.WorkThread2;

import java.lang.reflect.Method;

/**
 * Android消息机制--Handle运行机制
 *
 * @author zhongyao
 */
public class MainActivity extends Activity implements MyInterface, OnClickListener {

    public final static int SUCCESS = 0;
    public final static String TAG = "yao";
    public static final int SENDMSG = 1;
    public static final int SENDTOTARGET = 2;
    public MyHandler handler;
    private TextView tv1;
    private Button btn_post;
    private Button btn_post_delayed;
    private Button btn_message_obtain;
    private Button btn_message_obtainHandler;
    private Button btn_sendMessageAtTime;
    private Button btn_idleHandler;
    private Button workThreadSend;
    private Button sendSyncBarrier, removeSyncBarrier, sendSyncMessage, sendAsyncMessage;
    private Handler mHandler = new Handler();

    private static Handler mSyncBarrierHandler;

    private static Handler mHandler3 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SENDMSG:
                    //通过Log可知不同的Handler发送的msg对应的target即是该Handler，有target做标记，
                    //就会Handler处理对应的msg，而不会发生错乱的情况。
                    Log.d(TAG, "mHandler3:" + msg.getTarget().toString());
                    break;
                case MESSAGE_TYPE_SYNC:
                    Log.d(TAG, "收到普通消息");
                    break;
                case MESSAGE_TYPE_ASYNC:
                    Log.d(TAG, "收到异步消息");
                    break;
                default:
                    break;
            }
        }
    };

    private Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            /**
             * 通过打印Log可知：
             * mHandler与mHandler2关联的Looper是一样的。
             * 经过查看源码，首先Loop.prepare()在本线程中保存一个Looper实例,然后该Looper实例保存一个MessageQueue对象；
             * 因为looper.prepare()只能调用一次，所以一个线程中只能有一个Looper实例，一个MessageQueue实例。
             */
            Log.d(TAG, "mHandler2关联的Looper为:" + Looper.myLooper());
            Log.d(TAG, "mHandler2关联的MessageQueue为:" + Looper.myQueue());
            /**
             * 上面说到了，一个线程中只能有一个Looper、MessageQueue，那么有两个Handler都发送msg到该MessageQueue中，
             * 那么如何准确判断Handler对应msg。这里我们就用到了target。
             */
            Log.d(TAG, "mHandler2:" + msg.getTarget().toString());
            if (msg.what == SENDMSG) {
                Log.d(TAG, "收到:" + msg.obj.toString());

            } else if (msg.what == SENDTOTARGET) {
                Log.d(TAG, "收到:" + msg.obj.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        initHanlder();

        initSyncBarrierHandler();


        new WorkThread(handler).start();
    }

    private void init() {
        tv1 = (TextView) findViewById(R.id.tv1);

        btn_post = (Button) findViewById(R.id.btn_post);
        btn_post_delayed = (Button) findViewById(R.id.btn_post_delayed);
        btn_message_obtain = (Button) findViewById(R.id.btn_message_obtain);
        btn_message_obtainHandler = (Button) findViewById(R.id.btn_message_obtainHandler);
        btn_sendMessageAtTime = (Button) findViewById(R.id.btn_sendMessageAtTime);
        btn_idleHandler = (Button) findViewById(R.id.btn_idleHandler);
        workThreadSend = (Button) findViewById(R.id.workThreadSend);
        sendSyncBarrier = (Button) findViewById(R.id.sendSyncBarrier);
        removeSyncBarrier = (Button) findViewById(R.id.removeSyncBarrier);
        sendSyncMessage = (Button) findViewById(R.id.sendSyncMessage);
        sendAsyncMessage = (Button) findViewById(R.id.sendAsyncMessage);

        btn_post.setOnClickListener(this);
        btn_post_delayed.setOnClickListener(this);
        btn_message_obtain.setOnClickListener(this);
        btn_message_obtainHandler.setOnClickListener(this);
        btn_sendMessageAtTime.setOnClickListener(this);
        btn_idleHandler.setOnClickListener(this);
        workThreadSend.setOnClickListener(this);
        sendSyncBarrier.setOnClickListener(this);
        removeSyncBarrier.setOnClickListener(this);
        sendSyncMessage.setOnClickListener(this);
        sendAsyncMessage.setOnClickListener(this);
    }

    private void initHanlder() {

        handler = new MyHandler(this);

    }

    @Override
    public void refreshUI(Object obj) {
        tv1.setText(obj.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_post:
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        //使用post方式更新主线程
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                Log.d(TAG, "mHandler关联的Looper为:" + Looper.myLooper());
                                Log.d(TAG, "mHandler关联的MessageQueue为:" + Looper.myQueue());
                                if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                                    Log.d(TAG, "运行在--主--线程");
                                } else {
                                    Log.d(TAG, "运行在--子--线程");
                                }
                            }
                        });
                    }
                }).start();
                break;
            case R.id.btn_post_delayed:
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if (Looper.getMainLooper() == Looper.myLooper()) {
                                    Log.d(TAG, "运行在--主--线程");
                                } else {
                                    Log.d(TAG, "运行在--子--线程");
                                }
                            }
                        }, 3000);
                    }
                }).start();
                break;
            case R.id.btn_message_obtain:
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        /**
                         * Message.obtain()会从消息池中获取一个Message对象，
                         * 如果消息池中是空的，才会使用构造方法实例化一个新Message，这样有利于消息资源的利用。
                         */
                        Message msg = Message.obtain();
                        msg.what = SENDMSG;
                        msg.obj = "使用Message.obtain()+Handler.sendMessage(msg)发送消息";
                        mHandler2.sendMessage(msg);
                    }
                }).start();
                break;
            case R.id.btn_message_obtainHandler:
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        /**
                         * Message.obtain()方法具有多个重载方法，大致可以分为为两类:
                         * 一类是无需传递Handler对象，对于这类的方法，当填充好消息后，需要调用Handler.sendMessage()方法来发送消息到消息队列中。
                         * 第二类需要传递一个Handler对象，这类方法可以直接使用Message.sendToTarget()方法发送消息到消息队列中，
                         * 这是因为在Message对象中有一个私有的Handler类型的属性Target，当时obtain方法传递进一个Handler对象的时候，
                         * 会给Target属性赋值，当调用sendToTarget()方法的时候，实际在它内部还是调用的Target.sendMessage()方法。
                         */
                        Message msg = Message.obtain(mHandler2);
                        msg.what = SENDTOTARGET;
                        msg.obj = "使用Message.obtain(mHandler2)+Handler.sendToTarget发送消息()";
                        msg.sendToTarget();
                    }
                }).start();
                break;
            case R.id.btn_sendMessageAtTime:
                Log.d(TAG, "btn_sendMessageAtTime按钮被点击");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = Message.obtain();
                        msg.what = SENDMSG;
                        msg.obj = "sendMessageAtTime()";
                        mHandler3.sendMessageAtTime(msg, SystemClock.uptimeMillis() + 2000);
                        //						mHandler3.sendMessage(msg);
                    }
                }).start();
                break;

            case R.id.btn_idleHandler:
                /**
                 * IdleHandler应用
                 */
                DelayTaskDispatcher delayTaskDispatcher = new DelayTaskDispatcher();
                delayTaskDispatcher.addTask(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "task 1 执行");
                    }
                });

                delayTaskDispatcher.addTask(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "task 2 执行");
                    }
                });

                delayTaskDispatcher.start();
                break;

            case R.id.workThreadSend:
                WorkThread1 workThread1 = new WorkThread1(WorkThread2.getHandler2());
                workThread1.start();

                WorkThread2 workThread2 = new WorkThread2(WorkThread1.getHandler1());
                workThread2.start();
                break;

            case R.id.sendSyncBarrier:
                sendSyncBarrier();
                break;
            case R.id.removeSyncBarrier:
                removeSyncBarrier();
                break;
            case R.id.sendSyncMessage:
                sendSyncMessage();
                break;
            case R.id.sendAsyncMessage:
                sendAsyncMessage();
                break;
            default:
                break;
        }
    }

    int token;
    public static final int MESSAGE_TYPE_SYNC = 2;
    public static final int MESSAGE_TYPE_ASYNC = 3;


    private void initSyncBarrierHandler() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                /**
                 * 插入同步屏障之前，同步消息与异步消息接收无异；
                 * 插入同步屏障之后，只能打印发送的异步消息；
                 * 移除同步屏障后，之前发出的同步消息可以正常打印。
                 */
                mSyncBarrierHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == MESSAGE_TYPE_SYNC) {
                            Log.d(TAG, "收到普通消息");
                        } else if (msg.what == MESSAGE_TYPE_ASYNC) {
                            Log.d(TAG, "收到异步消息");
                        }
                    }
                };
                Looper.loop();
            }
        }).start();
    }

    /**
     * 往消息队列插入同步屏障
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendSyncBarrier() {
        try {
            Log.d(TAG, "插入同步屏障");
            MessageQueue queue = mSyncBarrierHandler.getLooper().getQueue();
            Method method = MessageQueue.class.getDeclaredMethod("postSyncBarrier");
            method.setAccessible(true);
            token = (int) method.invoke(queue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除同步屏障
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void removeSyncBarrier() {
        Log.d(TAG, "移除同步屏障");
        try {
            MessageQueue queue = mSyncBarrierHandler.getLooper().getQueue();
            Method method = MessageQueue.class.getDeclaredMethod("removeSyncBarrier", int.class);
            method.setAccessible(true);
            method.invoke(queue, token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 往消息队列插入普通消息
     */
    public void sendSyncMessage() {
        Log.d(TAG, "插入普通消息");
        Message message = Message.obtain();
        message.what = MESSAGE_TYPE_SYNC;
        mSyncBarrierHandler.sendMessageDelayed(message, 1000);
    }

    /**
     * 往消息队列插入异步消息
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void sendAsyncMessage() {
        Log.d(TAG, "插入异步消息");
        Message message = Message.obtain();
        message.what = MESSAGE_TYPE_ASYNC;
        message.setAsynchronous(true);
        mSyncBarrierHandler.sendMessageDelayed(message, 1000);
    }

}
