package com.myhandlerdemo;

import android.os.Handler;
import android.os.Message;

/**
 * @author hongri
 */
public class WorkThread extends Thread {
	private Handler handler;

	public WorkThread(Handler handler) {
		this.handler = handler;
	}
	@Override
	public void run() {
		super.run();
		/**
		 * 子线程耗时操作
		 */
		doTask();
	}
	
	private void doTask() {
		Message msg = new Message();
		
		msg.obj = "我是子线程获取过来的数据，该数据用于配合主线程更新UI界面";
		handler.sendMessage(msg);
	}
}
