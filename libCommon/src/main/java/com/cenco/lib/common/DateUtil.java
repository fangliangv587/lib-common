package com.cenco.lib.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.util.Log;

import com.cenco.lib.common.log.LogUtils;

public class DateUtil {

	public static final String FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_YMD = "yyyy-MM-dd";
	public static final String FORMAT_YMD1 = "yyyy年MM月dd日";
	public static final String FORMAT_MD1 = "MM-dd";
	public static final String FORMAT_MD2 = "MM/dd";
	public static final String FORMAT_HMS = "HH:mm:ss";
	public static final String FORMAT_HM = "HH:mm";
	public static final String FORMAT_M = "mm";
	
	public static final  String[] WEEKDAYS1 = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	public static final  String[] WEEKDAYS2 = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    public static final int MINUTE = 60 * 1000;
    public static final int HOUR = 60 * MINUTE;
    public static final int DAY = 24 * HOUR;


	/**
	 * 判断给定年份是不是闰年
	 *
	 * @return true 是闰年，false不是
	 */
	public static boolean isLeapYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return isLeapYear(year);
    }

	/**
	 * 判断是否是闰年
	 * @param year
	 * @return
	 */
	public static boolean isLeapYear(int year) {
		return (year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0);
	}


	/**
	 * date 转 string
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getDateString(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = formatter.format(date);
		return dateString;
	}

	public static String getDateString(String format) {
		return getDateString(new Date(), format);
	}

	public static String getDateString() {
		return getDateString(FORMAT_YMDHMS);
	}
	
	/**
	 * 获取周几
	 * @param date
	 * @return
	 */
	public static String getWeekOfDate(Date date,String[] weekdays) {
        if (weekdays == null || weekdays.length != 7) {
			return null;
		}
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekdays[w];
    }
	
	public static String getWeekOfDate(Date date) {
        return getWeekOfDate(date,WEEKDAYS1);
    }
	
	public static String getWeekOfDate(String[] weeks) {
        return getWeekOfDate(new Date(),weeks);
    }
	
	public static String getWeekOfDate() {
		return getWeekOfDate(new Date());
	}
	
	/**
	 * 目标日期是否在指定日期区间内(闭合区间) 
	 * @param destDate
	 * @param startDate
	 * @param stopDate
	 * @return
	 */
	public static boolean isInPeriodDate(Date destDate,Date startDate,Date stopDate){
		
		String dest = getDateString(destDate, FORMAT_YMDHMS);
		String start = getDateString(startDate, FORMAT_YMDHMS);
		String stop = getDateString(stopDate, FORMAT_YMDHMS);

            System.out.print("isInPeriodDate destDate= "+ dest+",start="+start+",stop="+stop);
            LogUtils.i("isInPeriodDate destDate= "+ dest+",start="+start+",stop="+stop);


		if (destDate.equals(startDate) || destDate.equals(stopDate)) {
			return true;
		}
		
		if (destDate.after(startDate) && destDate.before(stopDate)) {
			return true;
		}
		
		return false;
	}

    /**
     * 用于将日期转换成Calendar实例
     */
    public static Calendar convert(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 返回指定年数位移后的日期
     */
    public static Date yearOffset(Date date, int offset) {
        return offsetDate(date, Calendar.YEAR, offset);
    }

    /**
     * 返回指定月数位移后的日期
     */
    public static Date monthOffset(Date date, int offset) {
        return offsetDate(date, Calendar.MONTH, offset);
    }

    /**
     * 返回指定天数位移后的日期
     */
    public static Date dayOffset(Date date, int offset) {
        return offsetDate(date, Calendar.DATE, offset);
    }

    /**
     * 返回指定小时数位移后的日期
     *
     * @param date   参考时间
     * @param offset 位移数量，正数表示之前的时间，负数表示之后的时间
     */
    public static Date hourOffset(Date date, int offset) {
        return offsetDate(date, Calendar.HOUR_OF_DAY, offset);
    }

    /**
     * 返回指定日期相应位移后的日期
     *
     * @param date   参考日期
     * @param field  位移单位，见 {@link Calendar}
     * @param offset 位移数量，正数表示之后的时间，负数表示之前的时间
     * @return 位移后的日期
     */
    public static Date offsetDate(Date date, int field, int offset) {
        Calendar calendar = convert(date);
        calendar.add(field, offset);
        return calendar.getTime();
    }

    /**
     * 返回指定日期对应的0时0分0秒Date对象
     */
    public static Date getDateZero(Date date) {
        Calendar calendar = convert(date);
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 返回两个日期间的差异天数
     *
     * @param date1 参照日期
     * @param date2 比较日期
     * @return 参照日期与比较日期之间的天数差异，正数表示参照日期在比较日期之后，0表示两个日期同天，负数表示参照日期在比较日期之前
     */
    public static int dayDiff(Date date1, Date date2) {
        long diff = getDateZero(date1).getTime() - getDateZero(date2).getTime();
        return (int) (diff / DAY);
    }
}
