package com.aaron.library.frame.view;


import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

/**
 * @author linjinfa@126.com
 * @date 2013-9-12 下午3:33:09
 */
public class ViewUtil {

	/**
	 * 此方法直接照搬自网络上的一个下拉刷新的demo，计算View的width以及height
	 * @param child
	 */
	public static void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

}
