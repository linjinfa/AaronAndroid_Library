package com.aaron.library.frame.task;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.aaron.library.frame.application.CustomApplication;

/**
 * Task任务管理器
 * @author linjinfa@126.com
 * @date 2013-4-16 上午10:07:33
 */
public class TaskManager {
	
	private static TaskManager mManager = null;
	/**
	 * 任务队列
	 */
	private Queue<ITask> mTaskQueue;
	/**
	 * 管理视图控制器
	 */
	private ArrayList<IUIController> mControllerList;
	
	private TaskManager() {
		mTaskQueue = new LinkedList<ITask>();
		mControllerList = new ArrayList<IUIController>();
	}

	public synchronized static TaskManager getInstance() {
		if (mManager == null)
			mManager = new TaskManager();
		return mManager;
	}

	/**
	 * 有新任务进来，唤醒线程
	 * @param task
	 */
	public void addTask(ITask task) {
		if (!mTaskQueue.contains(task)) {
			mTaskQueue.offer(task);
		}
//		HuilianApplication.getPoolInstance().execute(TaskRunnable.getInstance());
		task.setFuture(CustomApplication.getPoolInstance().submit(TaskRunnable.getInstance()));
	}

	/**
	 * 取出队列的第一个
	 * @return
	 */
	public ITask getTask() {
		return mTaskQueue.poll();
	}

	/**
	 * 注册视图控制器
	 * @param con
	 */
	public void registerUIController(IUIController con) {
		if (!mControllerList.contains(con))
			mControllerList.add(con);
	}

	/**
	 * 移除视图控制器
	 * @param con
	 */
	public void unRegisterUIController(IUIController con) {
		if (mControllerList.contains(con))
			mControllerList.remove(con);
//		OpenHelperManager.releaseHelper();
	}

	/**
	 * 根据task的标识获取对应的视图控制器
	 * @param identification
	 * @return
	 */
	public IUIController getUIController(String identification) {
		for (IUIController controller : mControllerList) {
			if (controller.getIdentification().equals(identification)) {
				return controller;
			}
		}
		return null;
	}
	
	/**
	 * 清空任务队列及视图控制器集合
	 */
	public void destroy(){
		mTaskQueue.clear();
		mControllerList.clear();
	}
	
	/**
	 * 清空任务队列
	 */
	public void taskClear(){
		mTaskQueue.clear();
	}

}
