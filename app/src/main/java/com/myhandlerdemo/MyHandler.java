package com.myhandlerdemo;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author hongri
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

		myInterface.RefreshUI(msg.obj);
	}
}
