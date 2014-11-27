package com.aaron.library.frame.task;


import java.io.Serializable;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Task线程
 * @author linjinfa@126.com
 * @date 2013-4-16 上午10:07:18
 */
public class TaskRunnable implements Runnable{
	
	private static TaskRunnable mTaskRunnable = null;
	private Handler mHandler;

	private TaskRunnable(Handler mHandler) {
		super();
		this.mHandler = mHandler;
	}

	public synchronized static TaskRunnable getInstance(Handler mHandler) {
		if (mTaskRunnable == null)
			mTaskRunnable = new TaskRunnable(mHandler);
		return mTaskRunnable;
	}
	
	public synchronized static TaskRunnable getInstance() {
		return mTaskRunnable;
	}
	
	@Override
	public void run() {
		ITask task = TaskManager.getInstance().getTask();
		if (mHandler == null || task==null)
			return ;
		Object result = task.doTask();
		Bundle bundle = new Bundle();
		bundle.putInt("taskId", task.getTaskId());
		bundle.putString("identification", task.getmIdentification());
		bundle.putSerializable("result", (Serializable) result);
		
		Message msg = mHandler.obtainMessage();
		msg.setData(bundle);
		msg.sendToTarget();
	}
	
}
