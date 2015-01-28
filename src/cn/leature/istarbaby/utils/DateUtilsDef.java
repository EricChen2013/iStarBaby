/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: DateUtilsDef.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.utils
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 下午12:05:53
 * @version: V1.0  
 */
package cn.leature.istarbaby.utils;

/**
 * @ClassName: DateUtilsDef
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 下午12:05:53
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtilsDef {
	/**
	 * 将未指定格式的日期字符串转化成java.util.Date类型日期对象 <br>
	 * 
	 * @param date
	 *            ,待转换的日期字符串
	 * @return
	 * @throws ParseException
	 */
	public static Date parseStringToDate(String date) throws ParseException {

		String parse = date;
		parse = parse.replaceFirst("^[0-9]{4}([^0-9]?)", "yyyy$1");
		parse = parse.replaceFirst("^[0-9]{2}([^0-9]?)", "yy$1");
		parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}([^0-9]?)", "$1MM$2");
		parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}( ?)", "$1dd$2");
		parse = parse.replaceFirst("( )[0-9]{1,2}([^0-9]?)", "$1HH$2");
		parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}([^0-9]?)", "$1mm$2");
		parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}([^0-9]?)", "$1ss$2");

		SimpleDateFormat format = new SimpleDateFormat(parse);

		if (parse.equals(date)) {
			return null;
		}

		return format.parse(date);
	}

	/**
	 * 计算两个日期型的时间相差多少时间
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return: 相差一天以上的时候，返回 ？？天 一天之内的时候，返回 ？？小时？？分钟
	 */
	public static String dateDistance(Date startDate, Date endDate) {

		if (startDate == null || endDate == null) {
			return "";
		}

		long timeLong = endDate.getTime() - startDate.getTime();

		// 未来
		if (timeLong <= 0) {
			return "刚刚";
		}

		// 天数
		long day = timeLong / (24 * 60 * 60 * 1000);
		if (day > 0) {
			return String.valueOf(day) + "天前";
		}

		// 小时数
		long hour = timeLong / (60 * 60 * 1000);
		String hourStr = String.valueOf(hour);

		// 分钟数
		long minute = timeLong % (60 * 60 * 1000) / (60 * 1000);
		String minuteStr = String.valueOf(minute);

		String returnStr = "";
		if (hour > 0) {
			returnStr = hourStr + "小时";
		} else if (minute >= 0) {
			returnStr = minuteStr + "分钟";
		}

		return returnStr + "前";
	}

	public static String getDateBeforeNow(String dateString) {

		Date endDate = null;
		Date startDate = null;
		try {
			// 将字符串转化为日期
			endDate = DateUtilsDef.parseStringToDate(dateString);

			// 当前时间
			startDate = new Date(System.currentTimeMillis());
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}

		return DateUtilsDef.dateDistance(endDate, startDate);
	}

	public static String dateFormatString(String dateString, String format) {

		// 格式化日期Date
		SimpleDateFormat df = new SimpleDateFormat(format);

		Date date = null;
		try {
			date = DateUtilsDef.parseStringToDate(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (null == date) {
			return "";
		}

		return df.format(date);
	}

	/**
	 * 计算年龄
	 * 
	 * @param birthday
	 *            出生年月日(YYYYMMDD)
	 * @return: 年龄，返回 ？岁？个月
	 */
	public static String getAgeWithBirthday(String birthday) {

		// 取得当前日期
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		return calAgeBetweenString(birthday,
				String.format("%d%02d%02d", year, month, day));
	}

	public static String calAgeBetweenString(String start, String end) {

		// 默认，返回0
		String result = "-";

		if (start == null || end == null) {
			// 返回0
			return result;
		}

		start = start.trim();
		end = end.trim();
		if (start.length() < 8 || end.length() < 8) {
			// 位数不足
			return result;
		}

		try {

			// 将字符串转化为日期
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(DateUtilsDef.parseStringToDate(start));

			// 当前时间
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(DateUtilsDef.parseStringToDate(end));

			// 年差
			int yearDiff = endCal.get(Calendar.YEAR)
					- startCal.get(Calendar.YEAR);

			// 月差
			int monDiff = endCal.get(Calendar.MONTH)
					- startCal.get(Calendar.MONTH);

			// 日差
			int dayDiff = endCal.get(Calendar.DAY_OF_MONTH)
					- startCal.get(Calendar.DAY_OF_MONTH);

			if (dayDiff < 0) {
				// 日期小，月份-1
				monDiff -= 1;
			}

			if (monDiff < 0) {
				// 月份小，月份+12，年份-1
				monDiff += 12;
				yearDiff -= 1;
			}

			if (yearDiff < 0) {
				// 未来日期
				return result;

			} else if (yearDiff == 0) {
				if (monDiff == 0) {
					// 未满1个月
					result = "未满月";

				} else {
					// 1岁以下
					result = "满" + monDiff + "个月";
				}

			} else if (yearDiff < 3) {
				// 3岁以下
				result = yearDiff + "岁" + monDiff + "个月";

			} else {
				// 3岁以上
				result = "满" + yearDiff + "岁";

			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// 其他情况，返回0
		return result;
	}

	// 计算年龄，返回天数
	public static int calculateAgeWithBirthday(String birthday) {

		long distance = 0;
		// 取得当前日期
		try {
			Date birthdayDate = DateUtilsDef.parseStringToDate(birthday);

			Date nowDate = new Date(System.currentTimeMillis());

			distance = nowDate.getTime() - birthdayDate.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return (int) (distance / (1000 * 60 * 60 * 24));
	}

	// 计算年龄，返回岁数
	public static int calculateUserAgeWithBirthday(String birthday) {

		if (birthday == null || birthday.length() != 8) {
			return 0;
		}

		int yearDiff = 0;
		// 取得当前日期
		try {
			// 将字符串转化为日期
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(DateUtilsDef.parseStringToDate(birthday));

			// 当前时间
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(new Date(System.currentTimeMillis()));

			// 年差
			yearDiff = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);

			// 月差
			int monDiff = endCal.get(Calendar.MONTH)
					- startCal.get(Calendar.MONTH);

			// 日差
			int dayDiff = endCal.get(Calendar.DAY_OF_MONTH)
					- startCal.get(Calendar.DAY_OF_MONTH);

			if (dayDiff < 0) {
				// 日期小，月份-1
				monDiff -= 1;
			}

			if (monDiff < 0) {
				// 月份小，月份+12，年份-1
				monDiff += 12;
				yearDiff -= 1;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return yearDiff;
	}
}
