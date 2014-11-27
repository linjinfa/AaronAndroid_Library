package com.aaron.library.frame.util;


import android.content.Context;
import android.util.TypedValue;

/**
 * 单位转换操作类
 * @author linjinfa@126.com
 * @date 2013-4-19 下午5:28:11 
 */
public class DensityUtil {

	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dp2px(Context context, float dpValue) {  
        float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     * @param res
     * @param dp
     * @return
     */
    public static int dpTopx(Context context, float dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}
    
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dp(Context context, float pxValue) {  
        float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }
    
    /**
     * 根据手机的分辨率从 px 的单位 转成为 sp
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
    	float scale = context.getResources().getDisplayMetrics().density;  
		return (int) (pxValue / scale + 0.5f);
	}

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px
     * @param context
     * @param spValue
     * @return
     */
	public static int sp2px(Context context, float spValue) {
		float scale = context.getResources().getDisplayMetrics().density;  
		return (int) (spValue * scale + 0.5f);
	}
    
}
