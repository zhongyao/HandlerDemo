package com.myhandlerdemo;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author hongri
 * Handler运行机制需要底层的：
 * MessageQueue 消息队列---采用单链表的形式来存储消息列表
 * Looper 循环/消息循环器---Looper采用无线循环的方式去查找是否具有新消息，有的话就处理新消息，没有的话就一直等待。
 * ThreadLocal 作用是可以在每个线程中存储数据，即ThreadLocal可以在不同的线程中互不干扰的存储并提供数据，通过ThreadLocal可以轻松的
 * 获取到Looper。
 *
 * 消息队列的工作原理：
 * enqueueMessage：往消息队列中插入一条消息。
 * next：从消息队列中取出一条消息，并删除消息队列中的该条消息。
 * 
 */
public class MyHandler extends Handler {
	MyInterface myInterface;
	public MyHandler(MyInterface myInterface) {
		this.myInterface = myInterface;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);

		Log.d(MainActivity.TAG, msg.obj.toString());

		myInterface.refreshUI(msg.obj);
	}
}
