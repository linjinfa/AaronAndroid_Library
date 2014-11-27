package com.aaron.library.frame.task;


import android.os.Bundle;

import com.aaron.library.frame.pojo.MSG;

/**
 * 控制器接口
 * @author linjinfa@126.com
 * @date 2013-4-16 上午10:06:01
 */
public interface IUIController {
	
	/**
	 * 初始化控件
	 * @param savedInstanceState
	 */
	public void initUI(Bundle savedInstanceState);
	
	/**
	 * 初始化监听
	 * @param savedInstanceState
	 */
	public void initListener(Bundle savedInstanceState);
	
	/**
	 * 初始化数据
	 * @param savedInstanceState
	 */
	public void initData(Bundle savedInstanceState);
	
	/**
	 * 指定刷新的控件，及返回结果
	 * @param id
	 * @param params
	 */
	public void refreshUI(int id, MSG msg);

	/**
	 * IUIController 的标识
	 * @return
	 */
	public String getIdentification();
}
