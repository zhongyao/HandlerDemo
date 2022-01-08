package com.hongri.idlehandler;

import android.os.Looper;
import android.os.MessageQueue;

import java.util.LinkedList;
import java.util.Queue;

/**
 * IdleHandler是一个回调接口，可以通过MessageQueue的addIdleHandler添加实现类。
 * 当MessageQueue中的任务暂时处理完了（没有新任务或者下一个任务延时在之后），这个时候会回调这个接口
 */
public class DelayTaskDispatcher {

    private Queue<Runnable> delayTasks = new LinkedList<>();

    private MessageQueue.IdleHandler mIdleHandler = new MessageQueue.IdleHandler() {
        /**
         * @return true 表示可以反复执行该方法，即执行后还可以再次执行；
         *         false 表示执行完该方法后会移除该IdleHandler，即只执行一次。
         */
        @Override
        public boolean queueIdle() {
            if (delayTasks.size() > 0) {
                Runnable task = delayTasks.poll();
                if (task != null) {
                    task.run();
                }
            }
            return !delayTasks.isEmpty();
        }
    };

    public void addTask(Runnable runnable) {
        delayTasks.add(runnable);
    }

    public void start() {
        Looper.myQueue().addIdleHandler(mIdleHandler);
    }
}
