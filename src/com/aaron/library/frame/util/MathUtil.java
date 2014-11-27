package com.aaron.library.frame.util;


import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import roboguice.util.Strings;
import android.text.TextUtils;

/**
 * 数学帮助类
 */
public final class MathUtil {
	
	/**
	 * 取整四舍五入
	 * @param num
	 * @return
	 */
	public static BigDecimal with0DEC(String num){
		return new BigDecimal(num).setScale(0, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 保留一位小数  四舍五入
	 * @param f
	 * @return
	 */
	public static float with1DEC(float f) {
		return (float)(Math.round(f*10))/10; 
	}
	public static String with1DEC(String f) {
		if(!isNumber(f))
			return "";
		return ""+((float)(Math.round(Float.valueOf(f)*10))/10); 
	}
	/**
	 * 保留两位小数  四舍五入
	 * @param f
	 * @return
	 */
	public static float with2DEC(float f) {
		return (float)(Math.round(f*100))/100; 
	}
	/**
	 * 保留三位小数  四舍五入
	 * @param f
	 * @return
	 */
	public static float with3DEC(float f) {
		return (float)(Math.round(f*1000))/1000; 
	}
	/**
	 * 保留四位小数  四舍五入
	 * @param f
	 * @return
	 */
	public static float with4DEC(float f) {
		return (float)(Math.round(f*10000))/10000; 
	}
	/**
	 * 保留五位小数  四舍五入
	 * @param f
	 * @return
	 */
	public static float with5DEC(float f) {
		return (float)(Math.round(f*100000))/100000; 
	}
	
	/**
	 * 判断是否是非负数
	 * @param num
	 * @return
	 */
	public static boolean isNumber(String num){
		if(Strings.isEmpty(num))
			return false;
		Pattern pattern = Pattern.compile("^\\d+$|\\d+\\.\\d+$");
		Matcher matcher = pattern.matcher(num);
		return matcher.find();
	}
	
	/**
	 * 格式化数字  例如：2.0-->2.0  2.1-->2.5  2.5-->2.5  2.6-->3.0
	 * @param num
	 * @return
	 */
	public static float formatNumMark(float num){
		int numInt = (int) num;
		float decimal = num - numInt;
		if(decimal>0 && decimal<0.5){
			return num+(0.5f-decimal);
		}else if(decimal>0.5){
			return numInt+1f;
		}else{
			return num;
		}
	}
	
	/**
	 * 判断是否手机号码
	 * @param phone
	 * @return
	 */
	public static boolean isPhone(String phone){
		if(TextUtils.isEmpty(phone))
			return false;
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(phone); 
		return m.matches();
	}
	
	/**
	 * 将手机号码的最后5为改为*
	 * @param phone
	 * @return
	 */
	public static String encryPhone(String phone){
		if(TextUtils.isEmpty(phone))
			return "";
		return phone.substring(0, 6)+"*****";
	}
	
	/**
	 * 克转成两	1克=0.02两
	 * @param gNum
	 * @return
	 */
	public static float calGToOunce(String gNum){
		if(TextUtils.isEmpty(gNum))
			return 0f;
		float gFloat = Float.parseFloat(gNum);
		return with2DEC(gFloat * 0.02f);
	}
	
	/**
	 * 两转成克	1两=50克
	 * @param ounce
	 * @return
	 */
	public static float calOunceToG(String ounce){
		if(TextUtils.isEmpty(ounce))
			return 0f;
		float ounceFloat = Float.parseFloat(ounce);
		return with2DEC(ounceFloat * 50f); 
	}
	
}
