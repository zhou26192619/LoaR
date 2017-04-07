package com.loar.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期转换格式 从string ==> date
     *
     * @param date
     * @param pattern 默认yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date stringToDatetime(String date, String pattern) {
        Date result = null;
        try {
            if (pattern == null) {
                pattern = DEFAULT_PATTERN;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(pattern,
                    Locale.getDefault());
            result = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 日期转换格式 从 date ==> string
     *
     * @param date
     * @param pattern 默认yyyy
     *                -MM-dd HH:mm:ss
     * @return
     */
    public static String datetimeToString(Date date, String pattern) {
        String result = null;
        if (pattern == null) {
            pattern = DEFAULT_PATTERN;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern,
                Locale.getDefault());
        result = sdf.format(date);
        return result;
    }

    /**
     * 日期转换格式
     *
     * @param fromPattern 默认yyyy-MM-dd HH:mm:ss
     * @param toPattern   默认yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String datetimeTurnPattern(String strDate,
                                             String fromPattern, String toPattern) {
        if (fromPattern == null) {
            fromPattern = DEFAULT_PATTERN;
        }
        if (toPattern == null) {
            toPattern = DEFAULT_PATTERN;
        }
        return datetimeToString(stringToDatetime(strDate, fromPattern),
                toPattern);
    }

    /**
     * 计算和今天的偏移后的日期
     *
     * @param date
     * @param offset  偏移量 毫秒值, 负数为向前，正数为向后
     * @param pattern 返回的格式
     * @return 有问题, 不要用
     */
    public static String offsetByDate(Date date, long offset, String pattern) {
        long time = date.getTime() + offset;
        return datetimeToString(new Date(time), pattern);
    }

    /**
     * 计算和今天的偏移后的日期
     * <p/>
     * 偏移量 , 负数为向前，正数为向后
     *
     * @return 年月日数组int[3] ,注：month从0开始的
     */
    public static int[] offsetByCalendar(String strDate, int offsetY,
                                         int offsetM, int offsetD, int offsetMS) {
        Date date = stringToDatetime(strDate, null);
        return offsetByCalendar(date, offsetY, offsetM, offsetD, offsetMS);
    }

    /**
     * 计算和今天的偏移后的日期
     * <p/>
     * 偏移量 , 负数为向前，正数为向后
     *
     * @return 年月日时分秒数组int[6] ,注：month从0开始的
     */
    public static int[] offsetByCalendar(Date date, int offsetY, int offsetM,
                                         int offsetD, int offsetMS) {
        int[] ss = new int[6];
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, offsetMS);
        calendar.add(Calendar.YEAR, offsetY);
        calendar.add(Calendar.MONTH, offsetM);
        calendar.add(Calendar.DATE, offsetD);
        ss[0] = calendar.get(Calendar.YEAR);
        ss[1] = calendar.get(Calendar.MONTH);
        ss[2] = calendar.get(Calendar.DAY_OF_MONTH);
        ss[3] = calendar.get(Calendar.HOUR_OF_DAY);
        ss[4] = calendar.get(Calendar.MINUTE);
        ss[5] = calendar.get(Calendar.SECOND);
        return ss;
    }

    /**
     * 计算和今天的偏移后的日期
     *
     * @param date
     * @param returnPattern 返回的格式
     *                      偏移量 , 负数为向前，正数为向后
     * @return 年月日
     */
    public static String offsetByCalendar(Date date, int offsetY, int offsetM,
                                          int offsetD, int offsetMS, String returnPattern) {
        int[] ss = offsetByCalendar(date, offsetY, offsetM, offsetD, offsetMS);
        return datetimeTurnPattern(ss[0] + "-" + (ss[1] + 1) + "-" + ss[2]
                + " " + ss[3] + ":" + ss[4] + ":" + ss[5], null, returnPattern);

    }

    /**
     * 比较两个日期相差几天,strDate1-strDate2
     *
     * @param strDate1
     * @param strDate2
     * @param pattern  比较的精度 ，默认yyyy-MM-dd HH:mm:ss
     * @return 毫秒值, 0为相同，+为strDate1大，-为strDate2大
     */
    public static long compare2Datetimes(String strDate1, String strDate2,
                                         String pattern) {
        if (pattern == null) {
            pattern = DEFAULT_PATTERN;
        }
        long date1 = stringToDatetime(strDate1, pattern).getTime();
        long date2 = stringToDatetime(strDate2, pattern).getTime();
        return date1 - date2;
    }

    /**
     * 比较两个日期的大小
     *
     * @param strDate1
     * @param strDate2
     * @return 天数, 0为相同，+为strDate1大，-为strDate2大
     */
    public static long compare2Dates(String strDate1, String strDate2) {
        return compare2Datetimes(strDate1, strDate2, "yyyy-MM-dd") / 1000 / 60
                / 60 / 24;
    }

    /**
     * 获取calendar的年月日是时分秒
     *
     * @param date
     * @return
     */
    public static int[] getYTD(Date date) {
        int[] result = new int[6];
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        result[0] = calendar.get(Calendar.YEAR);
        result[1] = calendar.get(Calendar.MONTH);
        result[2] = calendar.get(Calendar.DAY_OF_MONTH);
        result[3] = calendar.get(Calendar.HOUR_OF_DAY);
        result[4] = calendar.get(Calendar.MINUTE);
        result[5] = calendar.get(Calendar.SECOND);
        return result;
    }

    /**
     * 获取calendar的年月日是时分秒
     *
     * @param strDate 至少 "yyyy-MM-dd"
     * @param pattern 默认 "yyyy-MM-dd hh:mm:ss"
     * @return
     */
    public static int[] getYTD(String strDate, String pattern) {
        return getYTD(DateTimeUtil.stringToDatetime(strDate, pattern));
    }

    /**
     * 计算年龄
     *
     * @param strDate 至少 "yyyy-MM-dd"
     * @return
     */
    public static int getAge(String strDate) {
        return DateTimeUtil.getYTD(new Date())[0]
                - DateTimeUtil.getYTD(strDate, "yyyy-MM-dd")[0];
    }

    /**
     * 获取calendar时间的某个字段
     *
     * @param date  时间
     * @param field 字段
     * @return
     */
    public static int getFieldOfCalendar(Date date, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(field);
    }

    /**
     * 获取calendar时间的某个字段
     *
     * @param date  时间
     * @param field 字段
     * @return
     */
    public static int getFieldOfCalendar(String date, int field) {
        return getFieldOfCalendar(stringToDatetime(date, null), field);
    }

    // <<====================================================================================================================

    /**
     * 显示今天、明天、后天，之后的就是直接显示日期 contrastDate-targetDate
     *
     * @param contrastDate 比较时间
     * @param targetDate   比较的对象
     *                     默认"yyyy-MM-dd"
     * @return
     */
    public static String displayDateNick(String contrastDate, String targetDate) {
        long temp = compare2Dates(contrastDate, targetDate);
        if (temp == 0) {
            return "今天";
        } else if (temp == 1) {
            return "明天";
        } else if (temp == 2) {
            return "后天";
        } else if (temp == -1) {
            return "昨天";
        } else if (temp == -2) {
            return "前天";
        }
        return contrastDate;
    }

    /**
     * 显示今天、明天、后天，之后的就是直接显示日期 contrastDate-targetDate
     *
     * @param contrastDate 比较时间
     * @param targetDate   比较的对象
     *                     默认"yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String displayDateTimeNick(String contrastDate,
                                             String targetDate) {
        long temp = stringToDatetime(contrastDate, null).getTime()
                - stringToDatetime(targetDate, null).getTime();
        if (temp <= 0) {
            temp = Math.abs(temp);
            temp = temp / 1000 / 60;
            if (temp == 0) {
                return "刚刚";
            }
            if (temp >= 1 && temp <= 59) {
                return temp + "分钟前";
            }
            temp = temp / 60;
            if (temp >= 1 && temp <= 23) {
                return temp + "小时前";
            }
        } else {
            temp = temp / 1000 / 60;
            if (temp == 0) {
                return "现在";
            }
            if (temp >= 1 && temp <= 59) {
                return temp + "分钟后";
            }
            temp = temp / 60;
            if (temp >= 1 && temp <= 23) {
                return temp + "小时后";
            }
        }
        return displayDateNick(contrastDate, targetDate);
    }

    /**
     * 将小写（1、2、3...）转成大写（一、二、三...）,失败就原样返回
     *
     * @param lowCase
     * @return
     */
    public static String turnMonthFont(String lowCase) {
        try {
            int low = Integer.valueOf(lowCase);
            switch (low) {
                case 1:
                    lowCase = "一";
                    break;
                case 2:
                    lowCase = "二";
                    break;
                case 3:
                    lowCase = "三";
                    break;
                case 4:
                    lowCase = "四";
                    break;
                case 5:
                    lowCase = "五";
                    break;
                case 6:
                    lowCase = "六";
                    break;
                case 7:
                    lowCase = "七";
                    break;
                case 8:
                    lowCase = "八";
                    break;
                case 9:
                    lowCase = "九";
                    break;
                case 10:
                    lowCase = "十";
                    break;
                case 11:
                    lowCase = "十一";
                    break;
                case 12:
                    lowCase = "十二";
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lowCase;
    }

    public static int getWeekNum(String week) {
        if (week.equals("星期一")) {
            return 1;
        } else if (week.equals("星期二")) {
            return 2;
        } else if (week.equals("星期三")) {
            return 3;
        } else if (week.equals("星期四")) {
            return 4;
        } else if (week.equals("星期五")) {
            return 5;
        } else if (week.equals("星期六")) {
            return 6;
        } else if (week.equals("星期天")) {
            return 7;
        }
        return 0;
    }

    /**
     * 统计时间段内的星期几的个数
     *
     * @param startDate
     * @param endDate
     * @param week
     * @return
     */
    public static int computeWeeksByDates(Date startDate, Date endDate,
                                          String week) {
        int count = 0;
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(startDate);
        calendar2.setTime(endDate);
        int w = getWeekNum(week);
        for (; ; ) {
            if (calendar1.get(Calendar.DAY_OF_WEEK) == w) {
                count++;
            }
            if (calendar1.getTimeInMillis() >= calendar2.getTimeInMillis()) {
                break;
            }
            calendar1.add(Calendar.DATE, 1);
        }
        return count;
    }

    /**
     * 统计时间段内的星期几的个数
     *
     * @param startDate
     * @param endDate
     * @param week
     * @param pattern
     * @return
     */
    public static int computeWeeksByDates(String startDate, String endDate,
                                          String week, String pattern) {
        return computeWeeksByDates(stringToDatetime(startDate, pattern),
                stringToDatetime(endDate, pattern), week);
    }

    /**
     * 通过星期查找第一个匹配到的星期的日期
     *
     * @param startDate
     * @param endDate
     * @param week
     * @return
     */
    public static String findDateByWeek(Date startDate, Date endDate,
                                        String week) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(startDate);
        calendar2.setTime(endDate);
        int w = getWeekNum(week);
        for (; ; ) {
            if (calendar1.get(Calendar.DAY_OF_WEEK) == w) {
                int[] ytd = getYTD(calendar1.getTime());
                return ytd[0] + "-" + (ytd[1] + 1) + "-" + ytd[2];
            }
            if (calendar1.getTimeInMillis() >= calendar2.getTimeInMillis()) {
                break;
            }
            calendar1.add(Calendar.DATE, 1);
        }
        return null;
    }

    /**
     * 通过星期查找第一个匹配到的星期的日期
     *
     * @param startDate
     * @param endDate
     * @param week
     * @param pattern
     * @return
     */
    public static String findDateByWeek(String startDate, String endDate,
                                        String week, String pattern) {
        return findDateByWeek(stringToDatetime(startDate, pattern),
                stringToDatetime(endDate, pattern), week);
    }

    /**
     * startDate-endDate 没想好
     *
     * @param startDate
     * @param endDate
     * @param pattern
     * @return 相差的天数
     */
    public static long compare(Object startDate, Object endDate, String pattern) {
        Date sd = null;
        Date ed = null;
        if (startDate instanceof String) {
            sd = stringToDatetime((String) startDate, pattern);
        } else if (startDate instanceof Date) {
            sd = (Date) startDate;
        }
        if (endDate instanceof String) {
            ed = stringToDatetime((String) endDate, pattern);
        } else if (endDate instanceof Date) {
            ed = (Date) endDate;
        }
        return 0;
    }

}
