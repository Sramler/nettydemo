package com.tymls.core.date;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/** @ClassName: DateTimeUtil @Description: 日期工具 @Author:gaobo @Author:luke @Company:fs */
@Slf4j
public class DateTimeUtil {
  public static final String DATEPARTITION = "-";

  /** 几种日期格式 */
  public static final String DATE_TIME_FORMART_ALL = "yyyy-MM-dd HH:mm:ss.SSS";

  public static final String DATE_TIME_FORMART_HIGH = "yyyy-MM-dd HH:mm:ss";
  public static final String DATE_TIME_FORMART_HIGH_M = "yyyy-MM-dd HH:mm";
  public static final String DATE_TIME_FORMART_HIGH_H = "yyyy-MM-dd HH";
  public static final String DATE_TIME_FORMART_SHORT = "yyyy-MM-dd";
  public static final String DATE_TIME_FORMART_SHORT_M = "yyyy-MM";
  public static final String DATE_TIME_FORMART_SHORT_Y = "yyyy";
  public static final String DATE_TIME_FORMART_TIME = "HH:mm:ss";
  public static final String DATE_TIME_FORMART_TIMESTAMP = "HHmmss";
  public static final String DATE_TIME_FORMART_MONTH_DAY = "yyyy年MM月dd日";
  public static final String DATE_TIME_FORMART_YMD = "yyyyMMdd";
  public static final String DATE_TIME_FORMART_ISODATE = "yyyy-MM-dd'T'HH:mm:ss'Z'";
  public static final String DATE_TIME_FORMART_STAMP = "yyyyMMddHHmmssSSS";
  public static final String DATE_TIME_FORMART_SIMPLE = "yyyyMMddHHmmss";

  /* _____________________________获取日期字符串操作开始______________________________ */
  /**
   * @Title: getCurDateFALL @Description: 返回yyyy-MM-dd hh:mm:ss.SSS格式日期String
   *
   * @return
   */
  public static String getCurDateFALL() {
    SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMART_ALL);
    return df.format(new Date());
  }

  /**
   * @Title: getCurDateFSTAMP @Description: 返回yyyyMMddhhmmssSSS格式String
   *
   * @return
   */
  public static String getCurDateFSTAMP() {
    SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMART_STAMP);
    return df.format(new Date());
  }

  /*
   * 将带有时间的日期去掉变为只有日期的值，
   * 例如 2015.01.01 05:30:40.123 ==> 2015.01.01 00:00:00.000
   */
  public static Date removeTime(Date date) {
    Calendar calstart = Calendar.getInstance();
    calstart.setTime(date);
    calstart.set(Calendar.HOUR_OF_DAY, 0);
    calstart.set(Calendar.MINUTE, 0);
    calstart.set(Calendar.SECOND, 0);
    calstart.set(Calendar.MILLISECOND, 0);
    return calstart.getTime();
  }

  /**
   * @Title: addTime
   *
   * @author: 吴佳隆
   * @data: 2019年4月28日 下午3:00:59 @Description: 获取当前日期最大时间 2015.01.01 05:30:40.123 ==> 2015.01.01
   *     23:59:59.999
   * @param date
   * @return Date
   * @throws
   */
  public static Date addTime(Date date) {
    Calendar calstart = Calendar.getInstance();
    calstart.setTime(date);
    calstart.set(Calendar.HOUR_OF_DAY, 23);
    calstart.set(Calendar.MINUTE, 59);
    calstart.set(Calendar.SECOND, 59);
    calstart.set(Calendar.MILLISECOND, 999);
    return calstart.getTime();
  }

  /*
   * 返回当天值,没有时间信息
   */
  public static Date today() {
    return removeTime(new Date());
  }

  /*
   * 计算一个日期增加或者减少一定天数后的日期，负数为减少
   * 例如 addDay('2015.12.21 05:30:40.123', 5)
   *  2015.12.26 05:30:40.123
   */
  public static Date addDay(Date date, int days) {
    Calendar calstart = Calendar.getInstance();
    calstart.setTime(date);
    calstart.add(Calendar.DATE, days);
    return calstart.getTime();
  }

  public static Date addHour(Date date, int hours) {
    Calendar calstart = Calendar.getInstance();
    calstart.setTime(date);
    calstart.add(Calendar.HOUR, hours);
    return calstart.getTime();
  }

  public static Date addSecond(Date date, int seconds) {
    Calendar calstart = Calendar.getInstance();
    calstart.setTime(date);
    calstart.add(Calendar.SECOND, seconds);
    return calstart.getTime();
  }

  public static Date addMilliseconds(Date date, long milliseconds) {
    Calendar calstart = Calendar.getInstance();
    calstart.setTime(date);
    long oneDay = 24 * 60 * 60 * 1000L;
    int day = (int) (milliseconds / oneDay);
    calstart.add(Calendar.DAY_OF_MONTH, day);
    calstart.add(Calendar.MILLISECOND, (int) (milliseconds - oneDay * day));
    return calstart.getTime();
  }

  public static Date addMinute(Date date, int minutes) {
    Calendar calstart = Calendar.getInstance();
    calstart.setTime(date);
    calstart.add(Calendar.MINUTE, minutes);
    return calstart.getTime();
  }

  public static int substractSecond(Date toTime, Date fromtime) {
    long time = (toTime.getTime() - fromtime.getTime()) / 1000;
    return (int) time;
  }

  /*
   * @Title: 计算一个日期增加或者减少一定月份后的日期，负数为减少
   */
  public static Date addMonth(Date date, int months) {
    Calendar calstart = Calendar.getInstance();
    calstart.setTime(date);
    calstart.add(Calendar.MONTH, months);
    return calstart.getTime();
  }

  /*
   * @Title: 计算几分钟之前或之后的时间
   */
  public static String getBeforeOrAfterTime(int minute) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, -minute);
    Date date = cal.getTime();
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sf.format(date);
  }

  /**
   * @Title: getCurDateFALLMD @Description: 返回yyyy年MM月dd日 格式日期String
   *
   * @return
   */
  public static String getCurDateFALLMD() {
    SimpleDateFormat sdmd = new SimpleDateFormat(DATE_TIME_FORMART_MONTH_DAY);
    return sdmd.format(new Date());
  }

  /**
   * @Title: getCurDateFHigh @Description: 返回yyyy-MM-dd hh:mm:ss格式日期String
   *
   * @return
   */
  public static String getCurDateFHigh() {
    return getCurDateFHigh(new Date());
  }

  /**
   * @Title: getCurDateFHigh @Description: 返回yyyy-MM-dd hh:mm:ss格式日期String
   *
   * @return
   */
  public static String getCurDateFHigh(Date date) {
    SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMART_HIGH);
    return df.format(date);
  }

  /**
   * @Title: getCurDateFHigh_M @Description: 返回yyyy-MM-dd hh:mm格式日期String
   *
   * @return
   */
  public static String getCurDateFHigh_M() {
    SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMART_HIGH_M);
    return df.format(new Date());
  }

  public static String getCurDateFSimple() {
    SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMART_SIMPLE);
    return df.format(new Date());
  }

  public static String getCurDateFHigh_M(Date date) {
    SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMART_HIGH_M);
    return df.format(date);
  }

  /**
   * @Title: getCurDateFHigh_H @Description: 返回yyyy-MM-dd hh格式日期String
   *
   * @return
   */
  public static String getCurDateFHigh_H() {
    SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMART_HIGH_H);
    return df.format(new Date());
  }

  /**
   * @Title: getCurDateFShort @Description: 返回yyyy-MM-dd格式日期String
   *
   * @return
   */
  public static String getCurDateFShort() {
    SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMART_SHORT);
    return df.format(new Date());
  }

  /**
   * @description: 返回yyyy-MM-dd格式日期String
   * @author: 吴佳隆
   * @date: 2018年12月15日
   * @param date
   * @return: String
   * @throws
   */
  public static String getCurDateFShort(Date date) {
    SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMART_SHORT);
    return df.format(date);
  }

  /**
   * @Title: getCurDateFShort_M @Description: 返回yyyy-MM格式日期String
   *
   * @return
   */
  public static String getCurDateFShort_M() {
    SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMART_SHORT_M);
    return df.format(new Date());
  }

  public static String getCurDateFShort_M(Date date) {
    SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMART_SHORT_M);
    return df.format(date);
  }

  /**
   * @Title: getCurDateFShort_Y @Description: 返回yyyy格式日期String
   *
   * @return
   */
  public static String getCurDateFShort_Y() {
    SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMART_SHORT_Y);
    return df.format(new Date());
  }

  public static String getCurDateFShort_Y(Date date) {
    SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMART_SHORT_Y);
    return df.format(date);
  }

  /**
   * @Title: getCurDate @Description: 得到相应格式的当前日期字符串
   *
   * @param：dateFormat(时间格式)
   * @return:相应格式的当前日期
   */
  public static String getCurDate(String dateFormat) {
    return new SimpleDateFormat(dateFormat).format(new Date());
  }

  /**
   * @Title: formatDateToString @Description: 将Date类型按照格式再转换成String
   *
   * @param：Date(需要转换的Date)
   * @param：dateFormat(时间格式)
   * @return:转换后的String类型的日期
   */
  public static String formatDateToString(Date date, String dateFormat) {
    return new SimpleDateFormat(dateFormat).format(date);
  }

  /**
   * @Title: formatCNYear @Description: 返回yyyy年格式日期String
   *
   * @param d
   * @return
   */
  public static String formatCNYear(Date d) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy年");
    return df.format(d);
  }

  /**
   * @Title: formatCNMonth @Description: 返回yyyy年MM月格式日期String
   *
   * @param d
   * @return
   */
  public static String formatCNMonth(Date d) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月");
    return df.format(d);
  }

  /**
   * @Title: formatCNDate @Description: 返回yyyy年MM月dd日格式日期String
   *
   * @param d
   * @return
   */
  public static String formatCNDate(Date d) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
    return df.format(d);
  }

  /**
   * @Title: formatCNTime @Description: 返回yyyy年MM月dd日 HH时mm分ss秒格式日期String
   *
   * @param d
   * @return
   */
  public static String formatCNTime(Date d) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
    return df.format(d);
  }

  /**
   * @Title: formatENTime @Description: 返回yyyy-MM-dd HH:mm:ss格式日期String
   *
   * @param d
   * @return
   */
  public static String formatENTime(Date d) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return df.format(d);
  }

  /* _____________________________获取日期字符串操作结束______________________________ */

  /* _____________________________获取日期类实例操作开始______________________________ */

  /**
   * @Title: getCurDate @Description: 当前日期java.util.Date
   *
   * @return:当前日期Date
   */
  public static Date getCurDate() {
    return new Date();
  }

  /**
   * @Title: getDateFALL @Description: 将（yyyy-MM-dd hh:mm:ss.SSS）格式的字符串转换为Date实例
   *
   * @param date （要转换的字符串）
   * @return 转换好的Date实例，不成功为null
   */
  public static Date getDateFALL(String date) {
    Date result = null;
    try {
      if (date == null || "".equals(date)) {
        return result;
      }
      SimpleDateFormat formatDate =
          new SimpleDateFormat(DATE_TIME_FORMART_ALL, Locale.SIMPLIFIED_CHINESE);
      result = formatDate.parse(date);
    } catch (Exception e) {
      result = null;
    }
    return result;
  }

  /**
   * @Title: getDateFHigh @Description: 将（yyyy-MM-dd hh:mm:ss）格式的字符串转换为Date实例
   *
   * @param date （要转换的字符串）
   * @return 转换好的Date实例，不成功为null
   */
  public static Date getDateFHigh(String date) {
    Date result = null;
    try {
      if (date == null || "".equals(date)) {
        return result;
      }
      SimpleDateFormat formatDate =
          new SimpleDateFormat(DATE_TIME_FORMART_HIGH, Locale.SIMPLIFIED_CHINESE);
      result = formatDate.parse(date);

    } catch (Exception e) {
      result = null;
    }
    return result;
  }

  /**
   * @Title: getDateFHigh_M @Description: 将（yyyy-MM-dd hh:mm）格式的字符串转换为Date实例
   *
   * @param date （要转换的字符串）
   * @return 转换好的Date实例，不成功为null
   */
  public static Date getDateFHigh_M(String date) {
    Date result = null;
    try {
      if (date == null || "".equals(date)) {
        return result;
      }
      SimpleDateFormat formatDate =
          new SimpleDateFormat(DATE_TIME_FORMART_HIGH_M, Locale.SIMPLIFIED_CHINESE);
      result = formatDate.parse(date);
    } catch (Exception e) {
      result = null;
    }
    return result;
  }

  /**
   * @Title: getDateFHigh_H @Description: 将（yyyy-MM-dd hh）格式的字符串转换为Date实例
   *
   * @param date （要转换的字符串）
   * @return 转换好的Date实例，不成功为null
   */
  public static Date getDateFHigh_H(String date) {
    Date result = null;
    try {
      if (date == null || "".equals(date)) {
        return result;
      }
      SimpleDateFormat formatDate =
          new SimpleDateFormat(DATE_TIME_FORMART_HIGH_H, Locale.SIMPLIFIED_CHINESE);
      result = formatDate.parse(date);
    } catch (Exception e) {
      result = null;
    }
    return result;
  }

  /**
   * @Title: getDateFShort @Description: 将（yyyy-MM-dd）格式的字符串转换为Date实例
   *
   * @param date （要转换的字符串）
   * @return 转换好的Date实例，不成功为null
   */
  public static Date getDateFShort(String date) {
    Date result = null;
    try {
      if (date == null || "".equals(date)) {
        return result;
      }
      SimpleDateFormat formatDate =
          new SimpleDateFormat(DATE_TIME_FORMART_SHORT, Locale.SIMPLIFIED_CHINESE);
      result = formatDate.parse(date);
    } catch (Exception e) {
      result = null;
    }
    return result;
  }

  /**
   * @Title: getDateFShort_M @Description: 将（yyyy-MM）格式的字符串转换为Date实例
   *
   * @param date （要转换的字符串）
   * @return 转换好的Date实例，不成功为null
   */
  public static Date getDateFShort_M(String date) {
    Date result = null;
    try {
      if (date == null || "".equals(date)) {
        return result;
      }
      SimpleDateFormat formatDate =
          new SimpleDateFormat(DATE_TIME_FORMART_SHORT_M, Locale.SIMPLIFIED_CHINESE);
      result = formatDate.parse(date);
    } catch (Exception e) {
      result = null;
    }
    return result;
  }

  /**
   * @Title: formatStringToDate @Description: 将String类型的日期按照格式转换成Date类型
   *
   * @param date 需要转换的日期字符串
   * @param format
   * @return
   */
  public static Date formatStringToDate(String date, String dateFormat) {
    ParsePosition pos = new ParsePosition(0);
    return new SimpleDateFormat(dateFormat).parse(date, pos);
  }

  /* _____________________________获取日期类实例操作开始______________________________ */

  /**
   * @Title: convertDateToCalendar @Description: java.util.Date转换为java.util.Calendar
   *
   * @param date (要转换的Date)
   * @return 转换后的Calendar
   */
  public static Calendar convertDateToCalendar(Date date) {
    Calendar calendar = null;
    if (date != null) {
      calendar = Calendar.getInstance();
      calendar.setTime(date);
    }
    return calendar;
  }

  /**
   * @Title: convertTimeStampToDate @Description: 将timestamp转换成date
   *
   * @param tt
   * @return
   */
  public static Date convertTimeStampToDate(Timestamp timestamp) {
    return new Date(timestamp.getTime());
  }

  /**
   * @Title: getCurTimestamp @Description: 返回java.sql.Timestamp
   *
   * @return
   */
  public static Timestamp getCurTimestamp() {
    return new Timestamp(System.currentTimeMillis());
  }

  /**
   * @Title: getCurSqlDate @Description: 返回当前日期 java.sql.Date
   *
   * @return
   */
  public static java.sql.Date getCurSqlDate() {
    return new java.sql.Date(getCurDate().getTime());
  }

  /**
   * @Title: getDateFShortAndWeek @Description: 获取当前日期和星期信息
   *
   * @param date
   * @param local EN;CN
   * @return
   */
  public static String getDateFShortAndWeek(String date, String local) {
    int week = getWeekdayOfDate(date);
    if (week == 0) {
      return date + " " + ("CN".equals(local) ? "星期天" : "Sunday");
    } else if (week == 1) {
      return date + " " + ("CN".equals(local) ? "星期一" : "Monday");
    } else if (week == 2) {
      return date + " " + ("CN".equals(local) ? "星期二" : "Tuesday");
    } else if (week == 3) {
      return date + " " + ("CN".equals(local) ? "星期三" : "Wednesday");
    } else if (week == 4) {
      return date + " " + ("CN".equals(local) ? "星期四" : "Thursday");
    } else if (week == 5) {
      return date + " " + ("CN".equals(local) ? "星期五" : "Friday");
    } else {
      return date + " " + ("CN".equals(local) ? "星期六" : "Saturday");
    }
  }

  /**
   * @Title: getWeekdayOfDate @Description: 获取yyyy-MM-dd是星期几
   *
   * @param datetime
   * @return
   */
  public static int getWeekdayOfDate(String date) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Calendar c = Calendar.getInstance();
    try {
      c.setTime(df.parse(date));
    } catch (Exception e) {
      e.printStackTrace();
    }
    int weekday = c.get(Calendar.DAY_OF_WEEK) - 1;
    return weekday;
  }

  /**
   * @Title: calculateDaysBetweenDate @Description: 计算两个日期之间有几天
   *
   * @param startDate
   * @param endDate
   * @return
   */
  public static int calculateDaysBetweenDate(Date start, Date end) {
    int intervalDay = 0;
    // 获取两个日期相隔天数
    long time = end.getTime() - start.getTime();
    long day = time / 3600000 / 24;
    intervalDay = Integer.parseInt(String.valueOf(day));
    return intervalDay;
  }

  /**
   * @Title: calculateDaysBetweenDate @Description: 计算两个日期之间有几天
   *
   * @param startDate
   * @param endDate
   * @return
   */
  public static int calculateDaysBetweenDate(String startDate, String endDate) {
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMART_HIGH_M);
    if (startDate.length() == 10) {
      startDate += " 00:00";
    }
    if (endDate.length() == 10) {
      endDate += " 00:00";
    }
    try {
      Date start = sdf.parse(startDate);
      Date end = sdf.parse(endDate);
      return calculateDaysBetweenDate(start, end);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return 0;
  }

  /**
   * @Title: calculateRemainHoursBetweenDate @Description: 计算两个日期之间除去整天后剩余的小时数
   *
   * @param startDate
   * @param endDate
   * @return
   */
  public static int calculateRemainHoursBetweenDate(String startDate, String endDate) {
    int intervalHour = 0;
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMART_HIGH_M);
    try {
      Date start = sdf.parse(startDate);
      Date end = sdf.parse(endDate);
      // 获取两个日期相隔天数
      long time = end.getTime() - start.getTime();
      long hour = time / 3600000 % 24;
      intervalHour = Integer.parseInt(String.valueOf(hour));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return intervalHour;
  }

  /**
   * @Title: calculateRemainHoursBetweenDate @Description: 计算两个日期之间除去整小时后剩余的分钟数
   *
   * @param startDate
   * @param endDate
   * @return
   */
  public static int calculateRemainMinutesBetweenDate(String startDate, String endDate) {
    int interval = 0;
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMART_HIGH_M);
    try {
      Date start = sdf.parse(startDate);
      Date end = sdf.parse(endDate);
      // 获取两个日期相隔天数
      long time = end.getTime() - start.getTime();
      long minutes = time / 60000 % 60;
      interval = Integer.parseInt(String.valueOf(minutes));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return interval;
  }

  /**
   * @Title: calculateMonthsBetweenDate @Description: 判断两个日期相差多少个月
   *
   * @param startDate
   * @param endDate
   * @return
   * @throws ParseException
   */
  public static String calculateMonthsBetweenDate(String startDate, String endDate)
      throws ParseException {
    String months = "";
    SimpleDateFormat f = new SimpleDateFormat(DATE_TIME_FORMART_SHORT);
    Date _startDate = f.parse(startDate);
    Date _endDate = f.parse(endDate);
    Calendar starCal = Calendar.getInstance();
    starCal.setTime(_startDate);
    int sYear = starCal.get(Calendar.YEAR);
    int sMonth = starCal.get(Calendar.MONTH);

    Calendar endCal = Calendar.getInstance();
    endCal.setTime(_endDate);
    int eYear = endCal.get(Calendar.YEAR);
    int eMonth = endCal.get(Calendar.MONTH);
    months = String.valueOf(Math.abs(((eYear - sYear) * 12 + (eMonth - sMonth))));
    return months;
  }

  /**
   * @Title: getMonthsBetweenDateList @Description: 获取两个日期之间月份列表
   *
   * @param startDate
   * @param endDate
   * @param order DESC ASC
   * @return
   */
  public static List<String> getMonthsBetweenDateList(Date startDate, Date endDate, String order) {
    List<String> list = new LinkedList<String>();
    Calendar starCal = Calendar.getInstance();
    starCal.setTime(startDate);
    Calendar endCal = Calendar.getInstance();
    endCal.setTime(endDate);
    if ("ASC".equals(order)) {
      while (starCal.compareTo(endCal) <= 0) {
        list.add(formatDateToString(starCal.getTime(), DATE_TIME_FORMART_SHORT_M));
        starCal.add(Calendar.MONTH, 1);
      }
    } else {
      while (endCal.compareTo(starCal) >= 0) {
        list.add(formatDateToString(endCal.getTime(), DATE_TIME_FORMART_SHORT_M));
        endCal.add(Calendar.MONTH, -1);
      }
    }
    return list;
  }

  /**
   * @Title: getDateMoveNDays @Description: 获取某日期移动N天后日期，整数往后推,负数往前移动。
   *
   * @param date
   * @param n
   * @return
   */
  public static Date getDateMoveNDays(Date date, int n) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, n);
    date = calendar.getTime();
    return date;
  }

  /**
   * @Title: getDateMoveNHours @Description: 获取某日期移动N小时后日期，整数往后推,负数往前移动。
   *
   * @param date
   * @param n
   * @return
   */
  public static Date getDateMoveNHours(Date date, int n) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    calendar.add(Calendar.HOUR, n);
    date = calendar.getTime();
    return date;
  }

  /**
   * @Title: getDateMoveNMonths @Description: 获取某日期移动N月后日期，整数往后推,负数往前移动。
   *
   * @param date
   * @param n
   * @return
   */
  public static Date getDateMoveNMonths(Date date, int n) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    calendar.add(Calendar.MONTH, n);
    date = calendar.getTime();
    return date;
  }

  /**
   * @Title: getFirstDayOfMonth @Description: 获取日期月的第一天
   *
   * @return
   */
  public static String getFirstDayOfMonth(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    return formatter.format(cal.getTime());
  }

  /**
   * @Title: getLastDayOfMonth @Description: 获取日期月的最后一天
   *
   * @return
   */
  public static String getLastDayOfMonth(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.roll(Calendar.DAY_OF_MONTH, -1);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    return formatter.format(cal.getTime());
  }

  /**
   * @Title: getDaysOfMonth @Description: 获取某年某月有多少天
   *
   * @param year
   * @param mon
   * @return
   */
  public static int getDaysOfMonth(int year, int mon) {
    GregorianCalendar d1 = new GregorianCalendar(year, mon - 1, 1);
    GregorianCalendar d2 = (GregorianCalendar) d1.clone();
    d2.add(Calendar.MONTH, 1);
    return (int) ((d2.getTimeInMillis() - d1.getTimeInMillis()) / 3600 / 1000 / 24);
  }

  /**
   * @Title: getDaysOfMonthList @Description: 获取某年某月日期的列表
   *
   * @param year
   * @param mon
   * @return
   */
  public static List<String> getDaysOfMonthList(int year, int mon) {
    int dayNum = getDaysOfMonth(year, mon);
    List<String> list = new LinkedList<String>();
    String mon_str = "" + mon;
    if (mon < 10) {
      mon_str = "0" + mon;
    }
    for (int i = 1; i <= dayNum; i++) {
      if (i < 10) {
        list.add(year + "-" + mon_str + "-0" + i);
      } else {
        list.add(year + "-" + mon_str + "-" + i);
      }
    }
    return list;
  }

  /**
   * @Title: minuteTransferHours @Description: 分钟转换成小时
   *
   * @param minute
   * @return
   */
  public static String minuteTransferHours(String minute) {
    if (minute == null || "".equals(minute)) {
      return "-";
    }
    try {
      Double min = Double.parseDouble(minute);
      String hours = (long) (min / 60) < 10 ? "0" + (long) (min / 60) : "" + (long) (min / 60);
      String minutes = (long) (min % 60) < 10 ? "0" + (long) (min % 60) : "" + (long) (min % 60);
      return hours + ":" + minutes;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "-";
  }

  /*--------------------------------------*/
  public static Date getCurrentGMTDate() {
    TimeZone defaultTimeZone = TimeZone.getDefault();
    Date date = Calendar.getInstance().getTime();
    date.setTime(date.getTime() - defaultTimeZone.getRawOffset());
    return date;
  }

  public static Timestamp getDayStart(Timestamp stamp) {
    return getDayStart(stamp, 0);
  }

  public static Timestamp getDayStart(Timestamp stamp, int daysLater) {
    Calendar tempCal = Calendar.getInstance();

    tempCal.setTime(new Date(stamp.getTime()));
    tempCal.set(
        tempCal.get(Calendar.YEAR),
        tempCal.get(Calendar.MONTH),
        tempCal.get(Calendar.DAY_OF_MONTH),
        0,
        0,
        0);
    tempCal.add(Calendar.DAY_OF_MONTH, daysLater);
    return new Timestamp(tempCal.getTime().getTime());
  }

  public static Timestamp getNextDayStart(Timestamp stamp) {
    return getDayStart(stamp, 1);
  }

  public static Timestamp getDayEnd(Timestamp stamp) {
    return getDayEnd(stamp, 0);
  }

  public static Timestamp getDayEnd(Timestamp stamp, int daysLater) {
    Calendar tempCal = Calendar.getInstance();

    tempCal.setTime(new Date(stamp.getTime()));
    tempCal.set(
        tempCal.get(Calendar.YEAR),
        tempCal.get(Calendar.MONTH),
        tempCal.get(Calendar.DAY_OF_MONTH),
        23,
        59,
        59);
    tempCal.add(Calendar.DAY_OF_MONTH, daysLater);
    return new Timestamp(tempCal.getTime().getTime());
  }

  public static String getDayEnd(Date date, int daysLater) {
    Calendar tempCal = Calendar.getInstance();
    tempCal.setTime(date);
    tempCal.set(
        tempCal.get(Calendar.YEAR),
        tempCal.get(Calendar.MONTH),
        tempCal.get(Calendar.DAY_OF_MONTH));
    tempCal.add(Calendar.DAY_OF_MONTH, daysLater);
    return formatDateToString(tempCal.getTime(), DATE_TIME_FORMART_SHORT);
  }

  public static String getHoursLaterDay(Date date, int hoursLater) {
    Calendar tempCal = Calendar.getInstance();
    tempCal.setTime(date);
    tempCal.set(
        tempCal.get(Calendar.YEAR),
        tempCal.get(Calendar.MONTH),
        tempCal.get(Calendar.DAY_OF_MONTH),
        Calendar.HOUR,
        00,
        00);
    tempCal.add(Calendar.HOUR, hoursLater);
    return formatDateToString(tempCal.getTime(), DATE_TIME_FORMART_SHORT);
  }

  public static Timestamp getWeekNo(Date d) {
    return null;
  }

  /*
   * 将时间戳转换为时间
   */
  public static String stampToDate(String s) {
    String res;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMART_SIMPLE);
    long lt = new Long(s);
    Date date = new Date(lt);
    res = simpleDateFormat.format(date);
    return res;
  }

  public static java.sql.Date toSqlDate(String date) {
    Date newDate = toDate(date, "00:00:00");

    if (newDate != null) {
      return new java.sql.Date(newDate.getTime());
    } else {
      return null;
    }
  }

  public static java.sql.Date toSqlDateForDateTime(String dateTime) {
    Date newDate = toDate(dateTime);
    if (newDate != null) {
      return new java.sql.Date(newDate.getTime());
    } else {
      return null;
    }
  }

  public static java.sql.Date toSqlDate(String monthStr, String dayStr, String yearStr) {
    Date newDate = toDate(monthStr, dayStr, yearStr, "0", "0", "0");

    if (newDate != null) {
      return new java.sql.Date(newDate.getTime());
    } else {
      return null;
    }
  }

  public static java.sql.Date toSqlDate(int month, int day, int year) {
    Date newDate = toDate(month, day, year, 0, 0, 0);
    if (newDate != null) {
      return new java.sql.Date(newDate.getTime());
    } else {
      return null;
    }
  }

  public static java.sql.Time toSqlTime(String time) {
    // java.util.Date newDate = toDate("1/1/1970", time);
    Date newDate = toDate("1970" + DATEPARTITION + "1" + DATEPARTITION + "1", time);
    if (newDate != null) {
      return new java.sql.Time(newDate.getTime());
    } else {
      return null;
    }
  }

  public static java.sql.Time toSqlTime(String hourStr, String minuteStr, String secondStr) {
    Date newDate = toDate("0", "0", "0", hourStr, minuteStr, secondStr);

    if (newDate != null) {
      return new java.sql.Time(newDate.getTime());
    } else {
      return null;
    }
  }

  public static java.sql.Time toSqlTime(int hour, int minute, int second) {
    Date newDate = toDate(0, 0, 0, hour, minute, second);

    if (newDate != null) {
      return new java.sql.Time(newDate.getTime());
    } else {
      return null;
    }
  }

  public static Timestamp toTimestamp(String dateTime) {

    Date newDate = toDate(dateTime);

    if (newDate != null) {
      return new Timestamp(newDate.getTime());
    } else {
      return null;
    }
  }

  public static Timestamp toTimestamp(String date, String time) {
    if ((date == null) || (time == null)) {
      return null;
    }
    return toTimestamp(date + " " + time);
  }

  public static Timestamp toTimestamp(
      String monthStr,
      String dayStr,
      String yearStr,
      String hourStr,
      String minuteStr,
      String secondStr) {
    Date newDate = toDate(monthStr, dayStr, yearStr, hourStr, minuteStr, secondStr);

    if (newDate != null) {
      return new Timestamp(newDate.getTime());
    } else {
      return null;
    }
  }

  public static Timestamp toTimestamp(
      int month, int day, int year, int hour, int minute, int second) {
    Date newDate = toDate(month, day, year, hour, minute, second);

    if (newDate != null) {
      return new Timestamp(newDate.getTime());
    } else {
      return null;
    }
  }

  public static Date toDate(String dateTime) {
    try {
      return new Date(
          DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.CHINA)
              .parse(dateTime)
              .getTime());
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Date toDate(String date, String time) {
    if ((date == null) || (time == null)) {
      return null;
    }
    return toDate(date + " " + time);
  }

  public static Date toDate(
      String monthStr,
      String dayStr,
      String yearStr,
      String hourStr,
      String minuteStr,
      String secondStr) {
    int month, day, year, hour, minute, second;

    try {
      month = Integer.parseInt(monthStr);
      day = Integer.parseInt(dayStr);
      year = Integer.parseInt(yearStr);
      hour = Integer.parseInt(hourStr);
      minute = Integer.parseInt(minuteStr);
      second = Integer.parseInt(secondStr);
    } catch (Exception e) {
      return null;
    }
    return toDate(month, day, year, hour, minute, second);
  }

  public static Date toDate(int month, int day, int year, int hour, int minute, int second) {
    Calendar calendar = Calendar.getInstance();

    try {
      calendar.set(year, month - 1, day, hour, minute, second);
    } catch (Exception e) {
      return null;
    }
    return new Date(calendar.getTime().getTime());
  }

  public static String toDateString(Date date) {
    if (date == null) {
      return "";
    }
    Calendar calendar = Calendar.getInstance();

    calendar.setTime(date);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int year = calendar.get(Calendar.YEAR);
    String monthStr;
    String dayStr;
    String yearStr;

    if (month < 10) {
      monthStr = "0" + month;
    } else {
      monthStr = "" + month;
    }
    if (day < 10) {
      dayStr = "0" + day;
    } else {
      dayStr = "" + day;
    }
    yearStr = "" + year;
    // return monthStr + "/" + dayStr + "/" + yearStr;
    return yearStr + "-" + monthStr + "-" + dayStr;
  }

  public static String toTimeString(Date date) {
    if (date == null) {
      return "";
    }
    Calendar calendar = Calendar.getInstance();

    calendar.setTime(date);
    return toTimeString(
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        calendar.get(Calendar.SECOND));
  }

  public static String toTimeString(int hour, int minute, int second) {
    String hourStr;
    String minuteStr;
    String secondStr;

    if (hour < 10) {
      hourStr = "0" + hour;
    } else {
      hourStr = "" + hour;
    }
    if (minute < 10) {
      minuteStr = "0" + minute;
    } else {
      minuteStr = "" + minute;
    }
    if (second < 10) {
      secondStr = "0" + second;
    } else {
      secondStr = "" + second;
    }
    if (second == 0) {
      return hourStr + ":" + minuteStr;
    } else {
      return hourStr + ":" + minuteStr + ":" + secondStr;
    }
  }

  public static String toDateTimeString(Date date) {
    if (date == null) {
      return "";
    }
    String dateString = toDateString(date);
    String timeString = toTimeString(date);

    if ((dateString != null) && (timeString != null)) {
      return dateString + " " + timeString;
    } else {
      return "";
    }
  }

  public static String toDateTimeString(java.sql.Date date) {
    if (date == null) {
      return "";
    }
    return toDateTimeString(new Date(date.getTime()));
  }

  public static Timestamp monthBegin() {
    Calendar mth = Calendar.getInstance();

    mth.set(Calendar.DAY_OF_MONTH, 1);
    mth.set(Calendar.HOUR_OF_DAY, 0);
    mth.set(Calendar.MINUTE, 0);
    mth.set(Calendar.SECOND, 0);
    mth.set(Calendar.AM_PM, Calendar.AM);
    return new Timestamp(mth.getTime().getTime());
  }

  public static Timestamp monthEnd() {
    Calendar mth = Calendar.getInstance();

    mth.set(Calendar.DAY_OF_MONTH, mth.getActualMaximum(Calendar.DAY_OF_MONTH));
    mth.set(Calendar.HOUR_OF_DAY, 23);
    mth.set(Calendar.MINUTE, 59);
    mth.set(Calendar.SECOND, 59);
    return new Timestamp(mth.getTime().getTime());
  }

  public static String getMonth(String date) {

    if ((date == null) || date.equals("")) {
      return "";
    }

    int dateSlash1 = date.indexOf(DATEPARTITION);
    int dateSlash2 = date.lastIndexOf(DATEPARTITION);

    return date.substring(dateSlash1 + 1, dateSlash2);
  }

  public static String getQuarter(String date) {
    int month = Integer.parseInt(getMonth(date));
    if (month < 4) {
      return "1";
    } else if (month < 7) {
      return "2";
    } else if (month < 10) {
      return "3";
    } else if (month < 13) {
      return "4";
    } else {
      return null;
    }
  }

  public static int getQuarterInt(String date) {
    int month = Integer.parseInt(getMonth(date));
    if (month < 4) {
      return 1;
    } else if (month < 7) {
      return 2;
    } else if (month < 10) {
      return 3;
    } else if (month < 13) {
      return 4;
    } else {
      return 0;
    }
  }

  public static String getYear(String date) {

    if ((date == null) || date.equals("")) {
      return "";
    }

    int dateSlash1 = date.indexOf(DATEPARTITION);
    // int dateSlash2 = date.lastIndexOf(DATEPARTITION);

    return date.substring(0, dateSlash1);
  }

  public static String getDay(String date) {

    if ((date == null) || date.equals("")) {
      return "";
    }

    int dateSlash2 = date.lastIndexOf(DATEPARTITION);

    return date.substring(
        dateSlash2 + 1, date.length() < dateSlash2 + 3 ? date.length() : dateSlash2 + 3);
  }

  public static String getHour(String date) {
    if ((date == null) || date.equals("")) {
      return "";
    }

    int dateSlash1 = date.indexOf(":");

    return date.substring(dateSlash1 - 2 < 0 ? 0 : dateSlash1 - 2, dateSlash1).trim();
  }

  public static String getMinute(String date) {
    if ((date == null) || date.equals("")) {
      return "";
    }

    int dateSlash1 = date.indexOf(":");
    int dateSlash2 = date.lastIndexOf(":");
    if (dateSlash1 == dateSlash2) {
      return date.substring(
              dateSlash1 + 1, date.length() < dateSlash2 + 3 ? date.length() : dateSlash2 + 3)
          .trim();
    }
    return date.substring(dateSlash1 + 1, dateSlash2).trim();
  }

  public static String getSecond(String date) {
    if ((date == null) || date.equals("")) {
      return "";
    }

    int dateSlash1 = date.indexOf(":");
    int dateSlash2 = date.lastIndexOf(":");
    if (dateSlash1 == dateSlash2) {
      return "0";
    }
    return date.substring(
            dateSlash2 + 1, date.length() < dateSlash2 + 3 ? date.length() : dateSlash2 + 3)
        .trim();
  }

  public static String getYear(java.sql.Date date) {
    String str = toDateTimeString(date);
    return getYear(str);
  }

  public static int getQuarterInt(java.sql.Date date) {
    String str = toDateTimeString(date);
    return getQuarterInt(str);
  }

  public static String getQuarter(java.sql.Date date) {
    String str = toDateTimeString(date);
    return getQuarter(str);
  }

  public static String getYear(Date date) {
    String str = toDateTimeString(date);
    return getYear(str);
  }

  public static String getMonth(java.sql.Date date) {
    String str = toDateTimeString(date);
    return getMonth(str);
  }

  public static String getMonth(Date date) {
    String str = toDateTimeString(date);
    return getMonth(str);
  }

  public static int getQuarterInt(Date date) {
    String str = toDateTimeString(date);
    return getQuarterInt(str);
  }

  public static String getQuarter(Date date) {
    String str = toDateTimeString(date);
    return getQuarter(str);
  }

  public static String getQuarter(String year, String month, boolean isNow) {
    if ((year == null) || (month == null) || "".equals(year) || "".equals(month)) {
      if (isNow) {
        return getQuarter(getCurDate());
      } else {
        return null;
      }
    }
    String str =
        new StringBuffer(year)
            .append(DATEPARTITION)
            .append(month)
            .append(DATEPARTITION)
            .append("01")
            .toString();
    return getQuarter(str);
  }

  public static int getQuarterInt(String year, String month, boolean isNow) {
    if ((year == null) || (month == null) || "".equals(year) || "".equals(month)) {
      if (isNow) {
        return getQuarterInt(getCurDate());
      } else {
        return 0;
      }
    }
    String str =
        new StringBuffer(year)
            .append(DATEPARTITION)
            .append(month)
            .append(DATEPARTITION)
            .append("01")
            .toString();
    return getQuarterInt(str);
  }

  public static String getDay(java.sql.Date date) {
    String str = toDateTimeString(date);
    return getDay(str);
  }

  public static String getDay(Date date) {
    String str = toDateTimeString(date);
    return getDay(str);
  }

  public static String getHour(java.sql.Date date) {
    String str = toDateTimeString(date);
    return getHour(str);
  }

  public static String getHour(Date date) {
    String str = toDateTimeString(date);
    return getHour(str);
  }

  public static String getMinute(java.sql.Date date) {
    String str = toDateTimeString(date);
    return getMinute(str);
  }

  public static String getMinute(Date date) {
    String str = toDateTimeString(date);
    return getMinute(str);
  }

  public static String getSecond(java.sql.Date date) {
    String str = toDateTimeString(date);
    return getSecond(str);
  }

  public static String getSecond(Date date) {
    String str = toDateTimeString(date);
    return getSecond(str);
  }

  public static String getWeek(Date date) {
    if (date == null) {
      return "";
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int weekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;

    if (weekNum == 0) {
      weekNum = 7;
    }
    return String.valueOf(weekNum);
  }

  public static String getWeek(java.sql.Date date) {
    if (date == null) {
      return "";
    }
    return getWeek(new Date(date.getTime()));
  }

  public static String getWeek(String date) {
    return getWeek(toSqlDate(date));
  }

  public static String getWeek() {
    Calendar calendar = Calendar.getInstance();
    int weekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    if (weekNum == 0) {
      weekNum = 7;
    }
    return String.valueOf(weekNum);
  }

  public static Timestamp getTimestamp(long time) {
    return new Timestamp(time);
  }

  public static String getTimeStr(long time) {
    return new Timestamp(time).toString();
  }

  public static String getNowTime() {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    String xzsj = sdf.format(date);
    return xzsj;
  }

  /*
   * 计算两个日期之间相差的毫秒数
   */
  public static long getExpendTime(String startTime, String endTime) {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date start;
    Date end;
    long time = 0;
    try {
      start = sdf.parse(startTime);
      end = sdf.parse(endTime);
      time = start.getTime() - end.getTime();

    } catch (ParseException e) {
      e.printStackTrace();
    }
    return time;
  }

  public static void countEnd(String desc, long startTime) {
    long time = new Date().getTime();
    log.debug(desc + new Timestamp(time));
    long sec = (time - startTime) / (1000 * 60);
    long miao = (time - startTime) % (1000 * 60) / 1000;
    long haomiao = (time - startTime) % (1000 * 60) % 1000;
    System.out.println(desc + sec + "-" + miao + "-" + haomiao);
    log.debug(desc + sec + "-" + miao + "-" + haomiao);
  }

  public static long countStart(String desc) {
    StringBuffer endS = new StringBuffer();
    long time = new Date().getTime();
    log.debug(endS.append(desc).append("[").append(new Timestamp(time)).append("]").toString());
    return time;
  }

  public static Timestamp getWestWeekStart(Timestamp stamp) {
    Calendar tempCal = Calendar.getInstance();

    tempCal.setTime(new Date(stamp.getTime()));
    tempCal.set(Calendar.DAY_OF_WEEK, tempCal.getActualMinimum(Calendar.DAY_OF_WEEK));
    tempCal.set(Calendar.HOUR_OF_DAY, 0);
    tempCal.set(Calendar.MINUTE, 0);
    tempCal.set(Calendar.SECOND, 0);
    return new Timestamp(tempCal.getTime().getTime());
  }

  public static Timestamp getWestWeekEnd(Timestamp stamp) {
    Calendar tempCal = Calendar.getInstance();

    tempCal.setTime(new Date(stamp.getTime()));
    tempCal.set(Calendar.DAY_OF_WEEK, tempCal.getActualMaximum(Calendar.DAY_OF_WEEK));
    tempCal.set(Calendar.HOUR_OF_DAY, 23);
    tempCal.set(Calendar.MINUTE, 59);
    tempCal.set(Calendar.SECOND, 59);
    return new Timestamp(tempCal.getTime().getTime());
  }

  public static Timestamp getWeekStart(Timestamp stamp) {
    if ("7".equals(getWeek(stamp.toString()))) {
      return getNextDayStart(getWestWeekStart(getDayStart(stamp, -1)));
    } else {
      return getNextDayStart(getWestWeekStart(stamp));
    }
  }

  public static Timestamp getWeekEnd(Timestamp stamp) {
    if ("7".equals(getWeek(stamp.toString()))) {
      return getDayEnd(getNextDayStart(getWestWeekEnd(getDayStart(stamp, -1))));
    } else {
      return getDayEnd(getNextDayStart(getWestWeekEnd(stamp)));
    }
  }

  public static Timestamp getMonthStart(Timestamp stamp) {
    Calendar tempCal = Calendar.getInstance();

    tempCal.setTime(new Date(stamp.getTime()));
    tempCal.set(Calendar.DAY_OF_MONTH, tempCal.getActualMinimum(Calendar.DAY_OF_MONTH));
    tempCal.set(Calendar.HOUR_OF_DAY, 0);
    tempCal.set(Calendar.MINUTE, 0);
    tempCal.set(Calendar.SECOND, 0);
    return new Timestamp(tempCal.getTime().getTime());
  }

  public static Timestamp getMonthEnd(Timestamp stamp) {
    Calendar tempCal = Calendar.getInstance();

    tempCal.setTime(new Date(stamp.getTime()));
    tempCal.set(Calendar.DAY_OF_MONTH, tempCal.getActualMaximum(Calendar.DAY_OF_MONTH));
    tempCal.set(Calendar.HOUR_OF_DAY, 23);
    tempCal.set(Calendar.MINUTE, 59);
    tempCal.set(Calendar.SECOND, 59);
    return new Timestamp(tempCal.getTime().getTime());
  }

  // public static String getQuarterEnd(String date){
  // Calendar tempCal = Calendar.getInstance();
  // tempCal.setTime(new
  // java.util.Date(DateUtility.getLongDate(date).longValue()));
  // tempCal.set(Calendar.MONTH, getQuarterInt(date)*3-1);
  // tempCal.set(Calendar.DAY_OF_MONTH,
  // tempCal.getActualMaximum(Calendar.DAY_OF_MONTH));
  // tempCal.set(Calendar.HOUR_OF_DAY, 23);
  // tempCal.set(Calendar.MINUTE, 59);
  // tempCal.set(Calendar.SECOND, 59);
  // return DateUtility.getStrDate(new Long(tempCal.getTime().getTime()), 0);
  //
  // }

  public static Timestamp getYearStart(Timestamp stamp) {
    Calendar tempCal = Calendar.getInstance();

    tempCal.setTime(new Date(stamp.getTime()));
    tempCal.set(Calendar.DAY_OF_YEAR, tempCal.getActualMinimum(Calendar.DAY_OF_YEAR));
    tempCal.set(Calendar.HOUR_OF_DAY, 0);
    tempCal.set(Calendar.MINUTE, 0);
    tempCal.set(Calendar.SECOND, 0);
    return new Timestamp(tempCal.getTime().getTime());
  }

  public static Timestamp getYearEnd(Timestamp stamp) {
    Calendar tempCal = Calendar.getInstance();

    tempCal.setTime(new Date(stamp.getTime()));
    tempCal.set(Calendar.DAY_OF_YEAR, tempCal.getActualMaximum(Calendar.DAY_OF_YEAR));
    tempCal.set(Calendar.HOUR_OF_DAY, 23);
    tempCal.set(Calendar.MINUTE, 59);
    tempCal.set(Calendar.SECOND, 59);
    return new Timestamp(tempCal.getTime().getTime());
  }

  public static int getWeekOfMonth(Date d) {
    Calendar calendar = Calendar.getInstance();
    if (d == null) {
      d = new Date();
    }
    calendar.setTime(d);
    int weekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    if (weekNum == 0) {
      weekNum = 7;
    }
    int v = Integer.parseInt(getDay(d)) - weekNum;
    if (v < 0) {
      return 1;
    } else {
      return (((Integer.parseInt(getDay(d)) - weekNum) + 1) / 7) + 2;
    }
  }

  public static int getWeekOfMonthFormDate(String dateString) {
    return getWeekOfMonth(toDate(dateString + " 00:00:01"));
  }

  public static int getWeekOfMonthFormDateTime(String dateTimeString) {
    return getWeekOfMonth(toDate(dateTimeString));
  }

  public static int getWeekOfMonth() {
    return getWeekOfMonth(null);
  }

  public static Date parseDate(String str) {
    try {
      return new SimpleDateFormat("yyyyMMdd HHmmss").parse(str);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return new Date();
  }

  public static Date parseDate(String format, String str) {
    try {
      return new SimpleDateFormat(format).parse(str);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return new Date();
  }

  /**
   * @Title: compareDate @Description: 将所给日期与当前日期作比较，判断出是今天、昨天还是前天
   *
   * @param date (所要比较的日期)
   * @return
   */
  public static String compareDate(Date date) {
    String balance = String.valueOf((new Date().getTime() - date.getTime()) / (3600 * 24 * 1000));
    // 今天
    if ("0".equals(balance)) {
      return "今天";
    }
    // 昨天
    else if ("1".equals(balance)) {
      return "昨天";
    }
    // 前天
    else if ("2".equals(balance)) {
      return "前天";
    }
    return formatDateToString(date, DATE_TIME_FORMART_SHORT);
  }

  /*
   * 计算Num时间以前的当前时间
   *
   * @para num
   *
   * @ auth wyt
   */
  public static Date getNumDate(int num) {
    GregorianCalendar gregorianCal = new GregorianCalendar();
    gregorianCal.set(Calendar.MONTH, gregorianCal.get(Calendar.MONTH) - num);
    return gregorianCal.getTime();
  }

  /**
   * 计算两个日期间相差的年月日
   *
   * @param startDate
   * @param endDate
   * @return
   */
  public static String calculateTimeDifference(String startDate, String endDate) {
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMART_HIGH);
    try {
      Date start = sdf.parse(startDate);
      Date end = sdf.parse(endDate);

      // 获取两个日期相隔时间
      long time = end.getTime() - start.getTime();

      // 此处未考虑溢出
      if (time < 0) {
        time *= -1;
      }

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date(time));

      int year = calendar.get(Calendar.YEAR) - 1970;
      int month = calendar.get(Calendar.MONTH);
      int day = calendar.get(Calendar.DATE) - 1;

      String result = year + "年" + month + "个月" + day + "天";

      return result;
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * 时间计算
   *
   * @param startTime
   * @param endTime
   * @return
   */
  public static Map<String, Integer> getTimeCalculation(Date startTime, Date endTime) {
    long minutes = (substractSecond(endTime, startTime) + 59) / 60;
    long day = DateCompareUtil.dateSubtract(startTime, endTime);
    long hour = (minutes / 60) - (day * 24);
    long minute = (minutes - (day * 24 * 60 + hour * 60));
    Map<String, Integer> paramsTime = new HashMap<String, Integer>();
    paramsTime.put("day", (int) day);
    paramsTime.put("hour", (int) hour);
    paramsTime.put("minute", (int) minute);
    return paramsTime;
  }

  /**
   * localDateTime转Date
   *
   * @description:
   * @author: zhc
   * @date: 2018年12月27日
   * @param localDateTime
   * @return
   * @return: Date
   * @throws
   */
  public static Date localTimeToDate(LocalDateTime localDateTime) {
    ZoneId zoneId = ZoneId.systemDefault();
    ZonedDateTime zdt = localDateTime.atZone(zoneId);
    Date date = Date.from(zdt.toInstant());
    return date;
  }

  public static void main(String[] args) {
    System.out.println(minuteTransferHours("13202232323343434348888888822222"));
    String time = "1513216663000";
    System.out.println(stampToDate(time));
    System.out.println(
        DateTimeUtil.parseDate(
            DateTimeUtil.DATE_TIME_FORMART_SIMPLE, DateTimeUtil.stampToDate(time)));
    // System.out.println(DateTimeUtil.getTheDay(new Date(), -1));
    // System.out.println("将util.Date转换成sql.Date"+new
    // java.sql.Date(getCurDate().getTime()));
    // System.out.println("将sql.Date转换成util.Date"+new
    // Date(nowSqlDate().getTime()));
    // System.out.println("当前日期java.util.Date#function#getCurDate---"+getCurDate());
    //
    // System.out.println("返回yyyy-MM-dd
    // HH:mm:ss.SSS格式的当前日期String#function#getCurDateFALL---"+getCurDateFALL());
    // System.out.println("返回yyyy-MM-dd
    // HH:mm:ss格式的当前日期String#function#getCurDateFHigh---"+getCurDateFHigh());
    // System.out.println("返回yyyy-MM-dd
    // 格式的当前日期String#function#getCurDateFShort---"+getCurDateFShort());
    // System.out.println("返回相应格式的当前日期String#function#getCurDate---"+getCurDate(DATE_TIME_FORMART_HIGH));
    // System.out.println("将指定Date类型按照格式转换成String#function#formatDateToString---"+formatDateToString(getCurDate(),DATE_TIME_FORMART_HIGH));
    //
    // System.out.println("将（yyyy-MM-dd
    // HH:mm:ss.SSS）格式的String转换为Date实例#function#getDateFALL---"+getDateFALL("2011-11-15
    // 11:53:22.998"));
    // System.out.println("将（yyyy-MM-dd
    // HH:mm:ss）格式的String转换为Date实例#function#getCurDateFHigh---"+getDateFHigh("2011-11-15
    // 11:53:22"));
    // System.out.println("将（yyyy-MM-dd）格式的String转换为Date实例#function#getCurDateFShort---"+getDateFShort("2011-11-15"));
    // System.out.println("将String类型的日期按照指定格式转换成Date实例#function#formatStringToDate---"+formatStringToDate("2011-11-15",DATE_TIME_FORMART_SHORT));

    // formatStringToDate("2011-11-15 11:53:22.998","YYYY-MM-DD HH24:MI:SS.FF");
    // try {
    // Date m_startTime = new
    // SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2011-11-15 11:53:22");
    // Date m_endTime = new
    // SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2011-11-15 11:53:22.998");
    // System.out.println("2011-11-15 11:53:22".length());
    // System.out.println(formatStringToDate("2011-11-15 23:59:59.999","yyyy-MM-dd
    // HH:mm:ss.SSS"));
    // } catch (ParseException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }

    // System.out.println(formatStringToDate("2011-11-15 11:53:22:999","yyyy-MM-dd
    // HH:mm:ss:SS"));
    // nowSqlDate();

    System.out.println(
        "计算两个日期之间有几天#function#calculateDay---"
            + calculateDaysBetweenDate("2012-01-01", "2013-03-31"));
    // System.out.println(getMonthsBetweenDateList(DateTimeUtil.getDateFShort_M("2012-09"),DateTimeUtil.getDateFShort_M("2012-12"),"ASC"));

    // System.out.println(DateTimeUtil.formatDateToString(DateTimeUtil.getDateMoveNMonths(DateTimeUtil.formatStringToDate("20121201",
    // "yyyyMMdd"), 0-2),"yyyyMM"));
    System.out.println("当月第一天" + getFirstDayOfMonth(new Date()));
    System.out.println("当月最后一天" + getLastDayOfMonth(new Date()));
  }
}
