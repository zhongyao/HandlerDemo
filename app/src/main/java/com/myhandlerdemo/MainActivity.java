package com.myhandlerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Handler机制
 * 
 * @author zhongyao。。。
 * 
 */
public class MainActivity extends Activity implements MyInterface,OnClickListener{

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
	private Handler mHandler = new Handler();
	
	private Handler mHandler2 = new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what == SENDMSG) {
				Log.d(TAG, "收到:"+msg.obj.toString()); 
				
			}else if (msg.what == SENDTOTARGET) {
				Log.d(TAG, "收到:"+msg.obj.toString());
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();

		initHanlder();

		new WorkThread(handler).start();
	}

	private void init() {
		tv1 = (TextView) findViewById(R.id.tv1);
		
		btn_post = (Button) findViewById(R.id.btn_post);
		btn_post_delayed = (Button) findViewById(R.id.btn_post_delayed);
		btn_message_obtain = (Button) findViewById(R.id.btn_message_obtain);
		btn_message_obtainHandler = (Button) findViewById(R.id.btn_message_obtainHandler);
		
		btn_post.setOnClickListener(this);
		btn_post_delayed.setOnClickListener(this);
		btn_message_obtain.setOnClickListener(this);
		btn_message_obtainHandler.setOnClickListener(this);
	}

	private void initHanlder() {

		handler = new MyHandler(this);

	}

	@Override
	public void RefreshUI(Object obj) {
		tv1.setText(obj.toString());
	}

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
							if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
								Log.d(TAG,"运行在--主--线程");
							}else {
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
							}else {
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
					Message msg = Message.obtain(mHandler2);
					msg.what = SENDTOTARGET;
					msg.obj = "使用Message.obtain(mHandler2)+Handler.sendToTarget发送消息()";
					msg.sendToTarget();
				}
			}).start();
			break;

		default:
			break;
		}
	}
}
