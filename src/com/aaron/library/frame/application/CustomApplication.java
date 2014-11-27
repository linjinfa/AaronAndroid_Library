package com.aaron.library.frame.application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.aaron.library.frame.pojo.MSG;
import com.aaron.library.frame.task.IUIController;
import com.aaron.library.frame.task.TaskManager;
import com.aaron.library.frame.task.TaskRunnable;

/**
 * 
 * @author linjinfa 331710168@qq.com
 * @date 2014年6月5日
 */
public class CustomApplication extends Application{

	private static CustomApplication instance;
	private static ExecutorService pool;
	
	/**
	 * 通知IUIController 刷新控件
	 */
	protected Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			if (bundle == null)
				return;
			int id = bundle.getInt("taskId");
			String identification = bundle.getString("identification");
			MSG result = (MSG) bundle.get("result");
			IUIController controller = TaskManager.getInstance().getUIController(identification);
			if (controller == null)
				return;
			controller.refreshUI(id, result);
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		TaskRunnable.getInstance(mHandler);
	}
	
	/**
	 * 单例  线程池
	 * @return
	 */
	public static ExecutorService getPoolInstance() {
		if(pool==null || (pool!=null && pool.isShutdown()))
			pool = Executors.newFixedThreadPool(10);
		return pool;
	}
	
	/**
	 * 清理
	 */
	public static void clear(){
		instance = null;
		if(pool!=null)
			pool.shutdownNow();
		pool = null;
	}

	public static CustomApplication getInstance() {
		return instance;
	}
	
}
