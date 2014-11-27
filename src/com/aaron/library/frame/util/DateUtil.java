package com.aaron.library.frame.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 日期操作类
 * @author linjinfa@126.com 
 * @version 2012-9-16 下午4:16:47
 */
public class DateUtil {
	
	public static final String DATE = "yyyy-MM-dd";
	public static final String DATEMONTHCZ = "yyyy年MM月";
	public static final String DATESTR = "yyyyMMdd";
	public static final String TIME = "HH:mm:ss";
	public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
	public static final String DATETIMET = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String PRECISE = "yyyy-MM-dd HH.mm.ss.SSS";
	public static final String $PRECISE = "yyyy-MM-dd$HH.mm.ss";
	public static final String DATETIMESTR = "yyyyMMddHHmmss";
	public static final String TIMESTR = "HHmmss";
	
	/**
	 * 将秒转成 "1小时 2分钟"的格式
	 * @param second
	 * @return
	 */
	public static String secondsFormat(long second) {
		long h = 0;
		long d = 0;
		@SuppressWarnings("unused")
		long s = 0;
		long temp = second % 3600;
		if (second >= 3600) { // 小时
			h = second / 3600;
			if (temp != 0) {
				if (temp > 60) {
					d = temp / 60;
					if (temp % 60 != 0) {
						s = temp % 60;
					}
				} else {
					s = temp;
				}
			}
		} else { // 分钟
			d = second / 60;
			if (second % 60 != 0) {
				s = second % 60;
			}
		}
		String str = "";
		if (h != 0) {
			str += h + "小时";
		}
		if (d != 0) {
			str += d + "分钟";
		}
		return str;
	}
	
	/**
	 * 根据日期返回对应当前月的最大天数 
	 * @param dateString
	 * @return
	 */
	public static int getMaxDays(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 根据日期返回年
	 * @param date
	 * @return	date为null时返回0
	 */
	public static int getYear(Date date) {
		return get(date, Calendar.YEAR);
	}
	
	/**
	 * 根据日期返回年
	 * @param date
	 * @return date为null时返回0
	 */
	public static int getYear(String date) {
		return get(toDate(date), Calendar.YEAR);
	}
	
	/**
	 * 根据日期返回月
	 * @param date
	 * @return date为null时返回0
	 */
	public static int getMonth(Date date) {
		return get(date, Calendar.MONTH);
	}
	
	/**
	 * 根据日期返回月
	 * @param date
	 * @return date为null时返回0
	 */
	public static int getMonth(String date) {
		return get(toDate(date), Calendar.MONTH);
	}
	
	/**
	 * 根据日期返回天
	 * @param date
	 * @return date为null时返回0
	 */
	public static int getDay(Date date) {
		return get(date, Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 根据日期返回天
	 * @param date
	 * @return date为null时返回0
	 */
	public static int getDay(String date) {
		return get(toDate(date), Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 获取当前小时 (24小时制)
	 * @return
	 */
	public static int getHour(){
		return get(getNowDate(), Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * 获取指定日期时间 的 小时 (24小时制)
	 * @param dateTimeStr
	 * @return
	 */
	public static int getHour(String dateTimeStr){
		return get(toDateTime(dateTimeStr), Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * 获取当前分钟 (24小时制)
	 * @return
	 */
	public static int getMin(){
		return get(getNowDate(), Calendar.MINUTE);
	}
	
	/**
	 * 获取指定日期时间的 分钟 (24小时)
	 * @param dateTimeStr
	 * @return
	 */
	public static int getMin(String dateTimeStr){
		return get(toDateTime(dateTimeStr), Calendar.MINUTE);
	}
	
	public static int get(Date date, int type) {
		if (date == null) {
			return 0;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(type);
	}

	/**
	 * 日期字符串转换成指定的格式
	 * @param dateString
	 * @param pattern
	 * @return
	 */
	public static String toPattern(String dateString, String pattern) {
		if (dateString == null || dateString.equals("")) {
			return null;
		}
		if (pattern == null || pattern.equals("")) {
			throw new RuntimeException("toPattern is null");
		}
		Date date = null;
		if(pattern.equals(DateUtil.DATE) || pattern.equals(DateUtil.DATESTR) || pattern.equals(DateUtil.DATEMONTHCZ))
			date = toDate(dateString);
		else
			date = toDateTime(dateString);
		return toPattern(date, pattern);
	}
	
	/**
	 * 日期字符串转换成Date	不包含Time
	 * @param dateTimeStr
	 * @return
	 */
	public static Date toDate(String dateTimeStr) {
		if (dateTimeStr == null || dateTimeStr.equals("")) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DATE);
		try {
			return sdf.parse(dateTimeStr);
		} catch (ParseException e1) {
			return null;
		}
	}
	
	/**
	 * 日期字符串转换成Date	包含Time
	 * @param dateTimeStr
	 * @return
	 */
	public static Date toDateTime(String dateTimeStr) {
		if (dateTimeStr == null || dateTimeStr.equals("")) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DATETIME);
		try {
			return sdf.parse(dateTimeStr);
		} catch (ParseException e1) {
			return null;
		}
	}
	
	/**
	 * 日期时间字符串转换成时间只包含Time
	 * @param dateTimeStr
	 * @return
	 */
	public static String toTime(Date date) {
		if(date==null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(TIME);
		try {
			return sdf.format(date);
		} catch (Exception e1) {
			return null;
		}
	}
	
	/**
	 * 日期Date转换成指定的格式
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String toPattern(Date date, String pattern) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	/**
	 * 将含有'T'的日期字符串转换为toPattern
	 * @param dateTime
	 * @return
	 */
	public static String toPatternT(String dateTime,String toPattern){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATETIMET);
			SimpleDateFormat output = new SimpleDateFormat(toPattern);
			Date d = sdf.parse(dateTime);
			return output.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**比较两个日期
	 * 判断DATE1是否在时间date2之前 
	 * @param DATE1
	 * @param DATE2
	 * @return	1：DATE1 在 DATE2之后 
	 * 		   -1：DATE1 在 DATE2之前
	 * 		   0：日期相等
	 */
	public static int compare_date(String DATE1, String DATE2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			return 0;
		}
	}
	
	/**
	 * 计算times开始经过seconds秒后的日期时间
	 * @param startDate
	 * @param seconds
	 * @return
	 */
	public static String getDateAfterTime(String startDate,long seconds){
		SimpleDateFormat format = new SimpleDateFormat(DATETIME);
		return format.format(toDateTime(startDate).getTime() + seconds*1000);
	}
	
	/**
	 * 
	 * @param datestr
	 *            日期字符串
	 * @param day
	 *            相对天数，为正数表示之后，为负数表示之前
	 * @return 指定日期字符串n天之前或者之后的日期
	 */
	public static String getBeforeAfterDate(String datestr, int day) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date olddate = null;
		try {
			df.setLenient(false);
			olddate = new Date(df.parse(datestr).getTime());
		} catch (ParseException e) {
			throw new RuntimeException("日期转换错误");
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(olddate);

		int Year = cal.get(Calendar.YEAR);
		int Month = cal.get(Calendar.MONTH);
		int Day = cal.get(Calendar.DAY_OF_MONTH);

		int NewDay = Day + day;

		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, NewDay);

		return df.format(new Date(cal.getTimeInMillis()));
	}
	
	/**
	 * 计算两个月份的月份差
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int diffMonth(String date1,String date2){
		try {
			Date d = toDate(date1);
			Date d1 = toDate(date2);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			c.setTime(d1);
			int year1 = c.get(Calendar.YEAR);
			int month1 = c.get(Calendar.MONTH);
			if (year == year1) {//两个日期相差几个月，即月份差
				return Math.abs(month1 - month);
			} else {//两个日期相差几个月，即月份差
				return Math.abs(12 * (year1 - year) + month1 - month);
			}
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * 计算两个日期间隔的天数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static Long getDaysBetween(Date startDate, Date endDate) {
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(startDate);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(endDate);
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);
		
		return (toCalendar.getTime().getTime() - fromCalendar.getTime()
				.getTime()) / (1000 * 60 * 60 * 24);
	}
	
	/**
	 * 计算两个日期时间间隔的毫秒数
	 * @param startDateTime	格式 yyyy-MM-dd HH:mm:ss
	 * @param endDateTime	格式 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static Long getTimesBetween(String startDateTime, String endDateTime){
		Calendar fromCalendar = Calendar.getInstance();
		Date startDate = toDateTime(startDateTime);
		fromCalendar.setTime(startDate);
		fromCalendar.set(Calendar.HOUR_OF_DAY, get(startDate, Calendar.HOUR_OF_DAY));
		fromCalendar.set(Calendar.MINUTE, get(startDate, Calendar.MINUTE));
		fromCalendar.set(Calendar.SECOND, get(startDate, Calendar.SECOND));
		fromCalendar.set(Calendar.MILLISECOND, get(startDate, Calendar.MILLISECOND));

		Calendar toCalendar = Calendar.getInstance();
		Date endDate = toDateTime(endDateTime);
		toCalendar.setTime(endDate);
		toCalendar.set(Calendar.HOUR_OF_DAY, get(endDate, Calendar.HOUR_OF_DAY));
		toCalendar.set(Calendar.MINUTE, get(endDate, Calendar.MINUTE));
		toCalendar.set(Calendar.SECOND, get(endDate, Calendar.SECOND));
		toCalendar.set(Calendar.MILLISECOND, get(endDate, Calendar.MILLISECOND));
		return toCalendar.getTime().getTime() - fromCalendar.getTime().getTime();
	}
	
	/**
	 * 实现给定某日期，判断是星期几 <br>
	 * date 必须yyyy-MM-dd
	 * @return <br>
	 */
	public static String getWeekday(String date) {
		if (date==null || "".equals(date) || date.length() == 0) {
			return "";
		}
		try {
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdw = new SimpleDateFormat("E");
			Date d = sd.parse(date);
			return sdw.format(d);
		} catch (Exception e) {}
		return "";
	}
	
	/**
	 * 计算某一星期几的日期
	 * @param delay	 推迟的周数，0本周，-1向前推迟一周，1下周，依次类推
	 * @param week	Calendar中的值    例如：Calendar.MONDAY ...等等
	 * @return
	 */
	public static String getDateByWeek(int delay,int week){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, delay * 7);
		cal.set(Calendar.DAY_OF_WEEK, week);
		return toPattern(cal.getTime(), DATE);
	}
	
	/**
	 * 返回本月第一天日期
	 * @return
	 */
	public static String getCurrentMonthFirst(){
		Calendar calendar = new GregorianCalendar();
	    calendar.set(Calendar.DATE, 1);
	    return toPattern(calendar.getTime(), DATE);
	}
	
	/**
	 * 返回本月最后一天日期
	 * @return
	 */
	public static String getCurrentMonthLast(){
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.DATE, 1);
		calendar.roll(Calendar.DATE, -1);
		return toPattern(calendar.getTime(), DATE);
	}
	
	/**
	 * 计算两个日期之间的所有日期	(不包含临界点)
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	public static List<String> getDateBetween(String dateFrom, String dateTo){
		List<String> list = new ArrayList<String>();
		String dateFormat = "yyyy-MM-dd";
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		if(dateFrom.equals(dateTo)){
		    return list;
		}
		String tmp;
		if(dateFrom.compareTo(dateTo) > 0){  //确保 dateFrom的日期不晚于dateTo
			tmp = dateFrom;
			dateFrom = dateTo;
			dateTo = tmp;
		}
		tmp = format.format(toDate(dateFrom).getTime() + 3600*24*1000);
        int num = 0;
        while(tmp.compareTo(dateTo) < 0){
			list.add(tmp);
        	num++;
        	tmp = format.format(toDate(tmp).getTime() + 3600*24*1000);
        }
        if(num == 0)
        	return list;
		return list;
	}
	
	/**
	 * 计算两个日期之间所有的时间(每隔60s)
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	public static List<String> getTimeBetween(String dateFrom, String dateTo){
		List<String> list = new ArrayList<String>();
		SimpleDateFormat format = new SimpleDateFormat(DATETIME);
		if(dateFrom.equals(dateTo)){
		    return list;
		}
		String tmp;
		if(dateFrom.compareTo(dateTo) > 0){  //确保 dateFrom的日期不晚于dateTo
			tmp = dateFrom;
			dateFrom = dateTo;
			dateTo = tmp;
		}
		tmp = format.format(toDate(dateFrom).getTime() + 60*1000);
        int num = 0;
        while(tmp.compareTo(dateTo) < 0){
			list.add(tmp);
        	num++;
        	tmp = format.format(toDateTime(tmp).getTime() + 60*1000);
        }
        if(num == 0)
        	return list;
		return list;
	}
	
	/**
	 * 返回当前日期时间
	 * 
	 * @return Date
	 */
	public static Date getNowDate() {
		return new Date(System.currentTimeMillis());
	}
	
	/**
	 * yyyy-MM-dd HH:mm:ss"格式返回当前时间
	 * 
	 * @return String
	 */
	public static String getNowString() {
		SimpleDateFormat sdf = new SimpleDateFormat(DATETIME);
		return sdf.format(getNowDate());
	}
	
	/**
	 * 根据指定格式返回当前日期时间
	 * 
	 * @return String
	 */
	public static String getNowString(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(getNowDate());
	}

}
