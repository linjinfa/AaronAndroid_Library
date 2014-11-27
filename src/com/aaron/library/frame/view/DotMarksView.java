package com.aaron.library.frame.view;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aaron.library.R;

/**
 * 引导标识Mark
 * @author linjinfa@126.com
 * @date 2013-9-11 上午10:31:26 
 */
public class DotMarksView extends LinearLayout{
	
	/**
	 * 焦点图片资源
	 */
	private Drawable guideFocus;
	/**
	 * 非焦点图片资源
	 */
	private Drawable guideDefault;

	public DotMarksView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DotMarksView(Context context) {
		super(context);
	}

	/**
	 * 初始化
	 */
	private void init(int size){
		if(guideFocus==null){
			guideFocus = getResources().getDrawable(R.drawable.guide_focus);
		}
		if(guideDefault==null){
			guideDefault = getResources().getDrawable(R.drawable.guide_default);
		}
		
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER);
		removeAllViews();
		ImageView imageView = null;
		for(int i=0;i<size;i++){
			imageView = new ImageView(getContext());
			if(i==0)
				imageView.setImageDrawable(guideFocus);
			else
				imageView.setImageDrawable(guideDefault);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			if(i!=size-1){
				params.rightMargin = 10;
			}
			addView(imageView, params);
		}
	}
	
	/**
	 * 设置Mark的数量
	 * @param size
	 */
	public void setMarkSize(int size){
		init(size);
	}
	
	/**
	 * 设置Mark选中
	 * @param position
	 */
	public void setSelect(int position){
		for(int i=0;i<getChildCount();i++){
			View view = getChildAt(i);
			if(view instanceof ImageView){
				ImageView imageView = (ImageView) view;
				if(i==position)
					imageView.setImageDrawable(guideFocus);
				else
					imageView.setImageDrawable(guideDefault);
			}
		}
	}

	/**
	 * 设置焦点图片资源
	 * @param resId
	 */
	public void setGuideFocus(int resId) {
		setGuideFocus(getResources().getDrawable(resId));
	}

	/**
	 * 设置非焦点图片资源
	 * @param resId
	 */
	public void setGuideDefault(int resId) {
		setGuideDefault(getResources().getDrawable(resId));
	}
	
	/**
	 * 设置焦点图片资源
	 * @param guideFocus
	 */
	public void setGuideFocus(Drawable guideFocus) {
		this.guideFocus = guideFocus;
	}
	
	/**
	 * 设置非焦点图片资源
	 * @param guideDefault
	 */
	public void setGuideDefault(Drawable guideDefault) {
		this.guideDefault = guideDefault;
	}
	
}
