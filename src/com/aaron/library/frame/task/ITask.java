package com.aaron.library.frame.task;


import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.Future;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.aaron.library.frame.annotation.InjectGuice;
import com.aaron.library.frame.pojo.MSG;
import com.google.gson.Gson;
import com.google.inject.Guice;

/**
 * 任务抽象类
 * @author linjinfa@126.com
 * @date 2013-4-16 上午10:06:17
 */
public abstract class ITask {
	
	/**
	 * 从第1条数据开始
	 */
	protected int start = 1;
	/**
	 * 每页显示的个数
	 */
	protected int total = 10;
	/**
	 * 结束条数
	 */
	protected int end = total;
	/**
	 *  任务Id
	 */
	protected int taskId;
	/**
	 *  指定任务所属的IUIController的标识
	 */
	protected String mIdentification;
	/**
	 * 是否读取本地数据
	 */
	protected boolean isLocal = true;
	/**
	 * 上下文
	 */
	protected Activity context;
	/**
	 * 请求的参数
	 */
	protected Map<String,String> params;
	protected Gson gson;
	private Future<?> future;

	/**
	 * 
	 * @param taskId
	 */
	public ITask(int taskId) {
		this.taskId = taskId;
		initField();
	}
	
	/**
	 * 
	 * @param taskId  任务Id
	 * @param params  请求参数
	 */
	public ITask(int taskId, Map<String, String> params) {
		super();
		this.taskId = taskId;
		this.params = params;
	}

	private void initField() {
		gson = new Gson();
		Field[] fields = getClass().getDeclaredFields();
		if(fields!=null && fields.length>0){
			for(Field field : fields){
				InjectGuice injectGuice = field.getAnnotation(InjectGuice.class);
				if(injectGuice!=null){
					try {
						field.setAccessible(true);
						field.set(this,Guice.createInjector().getInstance(field.getType()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 处理任务,并返回结果
	 * @return
	 */
	public abstract MSG doTask();
	
	/**
	 * 处理任务,并通过Handler返回结果
	 * @return
	 */
	public MSG doTask(Handler handler){
		return null;
	}
	
	/**
	 * 中断取消任务
	 * @return
	 */
	public boolean cancel(){
		if(future!=null){
			return future.cancel(true);
		}
		return false;
	}
	

	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setmIdentification(String mIdentification) {
		this.mIdentification = mIdentification;
	}
	public String getmIdentification() {
		return mIdentification;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setContext(Activity context) {
		this.context = context;
	}
	public Context getContext() {
		return context;
	}
	public void setFuture(Future<?> future) {
		this.future = future;
	}
	
}
