package com.loar.util;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by mabaobao-web-archetype
 */
public final class StringUtil {

    /**
     * 检验邮箱的正确性
     *
     * @param strEmail 输入的Email
     * @return 正确与否
     */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][ \\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\" +
            ".-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();

    }

    public static boolean isMobile(String str) {
        if (str == null) {
            return false;
        }
        return str.matches("1\\d{10}");

        // Pattern pattern = Pattern
        // .compile("((^(13|15|18|17)[0-9]{9}$)|(^0[1,2]{1} \\d{1}-?\\d{8}$)|
        // (^0[3-9]{1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|
        // (^0[3-9]{1}\\d{2}-?\\d{7,8}-(\\d{1,4})$))");
        // Matcher matcher = pattern.matcher(str);
        // if (matcher.find()) {
        // return true;
        // } else {
        // return false;
        // }

    }

    /**
     * type=0:字符串，type=1:数字
     *
     * @param args
     * @return
     */
    public static String nullToInteger(String args) {
        if (args != null) {
            args = args.trim();
            if (isInteger(args)) {
                return args;
            }
        }
        return "0";

    }

    /**
     * 将为null的字符串转换为空字符串
     *
     * @param args
     * @return
     */
    public static String nullToEmpty(String args) {
        if (args == null) {
            args = "";
        }
        return args;
    }

    /**
     * 整型判断
     *
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        if (str == null)
            return false;
        Pattern pattern = Pattern.compile("[0-9]+");
        return pattern.matcher(str).matches();
    }

    /**
     * 生成6位随即短信码
     *
     * @return
     */
    public static int getRandomSMSCode() {
        Random rand = new Random();
        int tmp = Math.abs(rand.nextInt());
        return tmp % (999999 - 100000 + 1) + 100000;
    }

    /**
     * 获取随机长度字符串
     *
     * @param length        字符串长度
     * @param includeLetter 是否包含字母
     * @return 长度为length的字符串
     */
    public static String getRandomString(int length, boolean includeLetter) {
        if (length < 1) {
            return "";
        }

        StringBuffer sb = new StringBuffer(length);
        sb.append(new Date().getTime());
        for (int i = 0; i < length; i++) {
            sb.append(includeLetter ? genRandomChar(i != 0)
                : genRandomDigit(i != 0));
        }
        return sb.toString();
    }

    /**
     * 获取随机字符
     *
     * @param allowZero
     * @return 随机字符
     */
    public static char genRandomChar(boolean allowZero) {
        int randomNumber = 0;

        do {
            randomNumber = (int) (Math.random() * 36);
        } while ((randomNumber == 0) && !allowZero);

        if (randomNumber < 10) {
            // log4JUtils.info("获取随机字符:" + (randomNumber + '0'));
            return (char) (randomNumber + '0');
        } else {
            // log4JUtils.info("获取随机字符:" + (randomNumber - 10 + 'a'));
            return (char) (randomNumber - 10 + 'a');
        }
    }

    /**
     * 获取随机数字
     *
     * @param allowZero 是否包含0
     * @return 随机数字
     */
    public static char genRandomDigit(boolean allowZero) {
        int randomNumber = 0;

        do {
            randomNumber = (int) (Math.random() * 10);
        } while ((randomNumber == 0) && !allowZero);

        // log4JUtils.info("获取随机数字:" + (randomNumber + '0'));
        return (char) (randomNumber + '0');
    }

    public static boolean isEmpty(String value, boolean trim, char... trimChars) {
        if (trim)
            return value == null || trim(value, trimChars).length() <= 0;
        return value == null || value.length() <= 0;
    }

    public static boolean isEmpty(String value, boolean trim) {
        return isEmpty(value, trim, ' ');
    }

    public static boolean isEmpty(String value) {
        return isEmpty(value, false);
    }

    public static boolean isEmptyOrEqualeToNull(String value) {
        if ("null".equalsIgnoreCase(value))
            return true;
        return isEmpty(value);
    }

    public static String nullSafeString(String value) {
        return value == null ? "" : value;
    }

    public static String trim(String value) {
        return trim(3, value, ' ');
    }

    public static String trim(String value, char... chars) {
        return trim(3, value, chars);
    }

    public static String trimStart(String value, char... chars) {
        return trim(1, value, chars);
    }

    public static String trimEnd(String value, char... chars) {
        return trim(2, value, chars);
    }

    private static String trim(int mode, String value, char... chars) {
        if (value == null || value.length() <= 0)
            return value;

        int startIndex = 0, endIndex = value.length(), index = 0;
        if (mode == 1 || mode == 3) {
            while (index < endIndex) {
                if (contains(chars, value.charAt(index++))) {
                    startIndex++;
                    continue;
                }
                break;
            }
        }

        if (startIndex >= endIndex)
            return "";

        if (mode == 2 || mode == 3) {
            index = endIndex - 1;
            while (index >= 0) {
                if (contains(chars, value.charAt(index--))) {
                    endIndex--;
                    continue;
                }
                break;
            }
        }

        if (startIndex >= endIndex)
            return "";

        return value.substring(startIndex, endIndex);
    }

    private static boolean contains(char[] chars, char chr) {
        if (chars == null || chars.length <= 0)
            return false;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == chr)
                return true;
        }
        return false;
    }

    /**
     * 手机号码验证
     *
     * @param phone
     * @return
     */
    public static boolean isMobileNumber(String phone) {
        Pattern pattern = Pattern
            .compile("^((\\+{0,1}86){0,1})((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    /**
     * 设置一个文本显示不同的字体
     *
     * @param tv      显示的文本框
     * @param title   需要加粗的标题
     * @param content 内容
     * @param context
     * @return
     */
    public static Spannable setDifferentFontTextView(TextView tv, String title,
                                                     String titleColor, int titleSize, String
                                                         content, String contentColor,
                                                     int contentSize, Context context) {
        if (title == null || content == null) {
            return null;
        }
        String source = title + content;
        Spannable WordtoSpan = new SpannableString(source);
        WordtoSpan.setSpan(new AbsoluteSizeSpan(titleSize, true), 0,
            title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        WordtoSpan.setSpan(new AbsoluteSizeSpan(contentSize, true),
            title.length(), source.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置字体前景色
        WordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor(titleColor)), 0,
            title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色为洋红色
        WordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor(contentColor)),
            title.length(), source.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色为洋红色

        // 设置字体背景色
        // WordtoSpan.setSpan(new BackgroundColorSpan(Color.CYAN),
        // title.length()+1, source.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // //设置背景色为青色
        if (tv != null) {
            tv.append(WordtoSpan);
        }
        return WordtoSpan;
    }
}