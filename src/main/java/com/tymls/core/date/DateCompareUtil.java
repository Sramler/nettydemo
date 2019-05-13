package com.tymls.core.date;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期比较
 *
 * @author luke by 2016年1月14日下午6:06:05
 */
public class DateCompareUtil {

  public static final int BEFORE = 1;
  public static final int EQUAL = 0;
  public static final int AFTER = -1;

  public static Date getNowDate() {
    DateFormat df = DateFormat.getDateInstance();

    try {
      return df.parse(df.format(new Date()));
    } catch (ParseException e) {
      return null;
    }
  }

  public static Date parseDate(String dateStr) {
    DateFormat df = DateFormat.getDateInstance();
    try {
      return df.parse(dateStr);
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 日期比较
   *
   * @param date1
   * @param date2
   * @return date1 before date2 return 1; date1 after date2 return -1 ;date1 equal date2 reutn 0;
   *     error return null
   */
  public static Integer compareDate(Date date1, Date date2) {

    Integer result = null;
    if (null == date1 || null == date2) {
    } else if (date1.before(date2)) {
      result = BEFORE;
    } else if (date1.after(date2)) {
      result = AFTER;
    } else {
      result = EQUAL;
    }
    return result;
  }

  /**
   * 获得当前日期的前一天日期
   *
   * @return YYYY-MM-dd
   */
  public static String getPreDayDate() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH, -1);
    Date date = cal.getTime();
    SimpleDateFormat sf = new SimpleDateFormat("YYYY-MM-dd");
    return sf.format(date);
  }

  /**
   * 根据开始日期和结束日期，计算日期差
   *
   * @param startDate
   * @param endDate
   * @return
   */
  public static long dateSubtract(Date startDate, Date endDate) {
    if (startDate == null || endDate == null) {
      return 0;
    } else {
      long dateLong1 = endDate.getTime();
      long dateLong2 = startDate.getTime();
      long day = (dateLong1 - dateLong2) / 1000 / 60 / 60 / 24;
      return day;
    }
  }

  /**
   * 根据开始日期和结束日期，计算日期差，返回分钟
   *
   * @param startDate
   * @param endDate
   * @return
   */
  public static long dateSubtractOfMinute(Date startDate, Date endDate) {
    if (startDate == null || endDate == null) {
      return 0;
    } else {
      long dateLong1 = endDate.getTime();
      long dateLong2 = startDate.getTime();
      long minute = (dateLong1 - dateLong2) / 1000 / 60;
      return minute;
    }
  }

  /**
   * 根据开始日期和结束日期，计算日期差，返回秒
   *
   * @param startDate
   * @param endDate
   * @return
   */
  public static long dateSubtractOfSecond(Date startDate, Date endDate) {
    if (startDate == null || endDate == null) {
      return 0;
    } else {
      long dateLong1 = endDate.getTime();
      long dateLong2 = startDate.getTime();
      long second = (dateLong1 - dateLong2) / 1000;
      return second;
    }
  }

  /**
   * 根据开始日期和结束日期的天数，不满一天按一天计算
   *
   * @param startDate
   * @param endDate
   * @return
   */
  public static long dateSubtractDay(Date startDate, Date endDate) {
    if (startDate == null || endDate == null) {
      return 0;
    } else {
      long seconds = dateSubtractOfSecond(startDate, endDate);
      long day = seconds / 60 / 60 / 24;
      long surPlus = seconds % (60 * 60 * 24);
      if (surPlus > 0) {
        day = day + 1;
      }
      return day;
    }
  }

  /**
   * @Title: getBetweenDates @Description: 获取两个日期间的所有日期字符串，可以传日年、年月、年月日等
   *
   * @author: 吴佳隆
   * @param @param startTime
   * @param @param endTime
   * @param @return
   * @return List<String>
   * @throws
   */
  @SuppressWarnings("unchecked")
  public static List<String> getBetweenDates(String startTime, String endTime) {
    // 先判断数据是否合法
    DateFormat dateFormat = null;
    Date startDate, endDate;
    Calendar startCalendar;
    List<String> result = new ArrayList<String>();
    if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
      return Collections.EMPTY_LIST;
    } else if (startTime.length() == 4 && endTime.length() == 4) { // 年
      Integer start, end;
      try {
        start = Integer.valueOf(startTime);
        end = Integer.valueOf(endTime);
        if (start > end) {
          return Collections.EMPTY_LIST;
        }
        for (int i = start; i <= end; i++) {
          result.add(i + "");
        }
      } catch (Exception e) {
        return Collections.EMPTY_LIST;
      }
    } else if (startTime.length() == 7 && endTime.length() == 7) { // 月
      try {
        dateFormat = new SimpleDateFormat("yyyy-MM");
        startDate = dateFormat.parse(startTime);
        endDate = dateFormat.parse(endTime);
      } catch (Exception e) {
        return Collections.EMPTY_LIST;
      }
      startCalendar = Calendar.getInstance();
      startCalendar.setTime(startDate);
      while (startDate.getTime() <= endDate.getTime()) {
        result.add(dateFormat.format(startCalendar.getTime()));
        startCalendar.add(Calendar.MONTH, 1);
        startDate = startCalendar.getTime();
      }
    } else if (startTime.length() == 10 && endTime.length() == 10) { // 日
      try {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        startDate = dateFormat.parse(startTime);
        endDate = dateFormat.parse(endTime);
      } catch (Exception e) {
        return Collections.EMPTY_LIST;
      }
      startCalendar = Calendar.getInstance();
      startCalendar.setTime(startDate);
      while (startDate.getTime() <= endDate.getTime()) {
        result.add(dateFormat.format(startCalendar.getTime()));
        startCalendar.add(Calendar.DAY_OF_YEAR, 1);
        startDate = startCalendar.getTime();
      }
    } else {
      return Collections.EMPTY_LIST;
    }
    return result;
  }

  /**
   * @description: 获取两个日期间的所有日期字符串
   * @author: 吴佳隆
   * @date: 2018年11月23日
   * @param startTime
   * @param endTime
   * @return
   * @return: List<String>
   * @throws
   */
  @SuppressWarnings("unchecked")
  public static List<String> getBetweenDates(Date startTime, Date endTime) {
    // 先判断数据是否合法
    if (null == startTime) {
      startTime = new Date();
    }
    if (null == endTime) {
      endTime = new Date();
    }
    if (startTime.getTime() - endTime.getTime() > 0) {
      return Collections.EMPTY_LIST;
    }
    DateFormat dateFormat = null;
    try {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    } catch (Exception e) {
      return Collections.EMPTY_LIST;
    }
    List<String> result = new ArrayList<String>();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startTime);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    while (startTime.getTime() <= endTime.getTime()) {
      result.add(dateFormat.format(calendar.getTime()));
      calendar.add(Calendar.DAY_OF_YEAR, 1);
      startTime = calendar.getTime();
    }
    return result;
  }

  /**
   * @Title: getBetweenDay
   *
   * @author: 吴佳隆
   * @data: 2019年4月8日 上午10:01:55 @Description: 获取两个日期之间的天数
   * @param startTime
   * @param endTime
   * @return int
   * @throws
   */
  public static int getBetweenDay(Date startTime, Date endTime) {
    // 先判断数据是否合法
    if (null == startTime || null == endTime) {
      return 0;
    }
    if (startTime.getTime() - endTime.getTime() > 0) {
      return 0;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startTime);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    int day = 0;
    while (startTime.getTime() <= endTime.getTime()) {
      calendar.add(Calendar.DAY_OF_YEAR, 1);
      startTime = calendar.getTime();
      day++;
    }
    return day;
  }

  public static void main(String[] args) {
    List<String> betweenDates = getBetweenDates(new Date(1542701592000L), new Date());
    System.out.println(betweenDates.toString());
  }
}
