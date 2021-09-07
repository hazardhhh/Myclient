package com.example.client.utils.string;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static String getPercentString(float percent) {
        return String.format(Locale.US, "%d%%", (int) (percent * 100));
    }

    /**
     * 删除字符串中的空白符
     *
     * @param content
     * @return String
     */
    public static String removeBlanks(String content) {
        if (content == null) {
            return null;
        }
        StringBuilder buff = new StringBuilder();
        buff.append(content);
        for (int i = buff.length() - 1; i >= 0; i--) {
            if (' ' == buff.charAt(i) || ('\n' == buff.charAt(i)) || ('\t' == buff.charAt(i))
                    || ('\r' == buff.charAt(i))) {
                buff.deleteCharAt(i);
            }
        }
        return buff.toString();
    }

    /**
     * 获取32位uuid
     *
     * @return
     */
    public static String get32UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static boolean isEmpty(String input) {
        return TextUtils.isEmpty(input);
    }

    /**
     * 生成唯一号
     *
     * @return
     */
    public static String get36UUID() {
        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString();
        return uniqueId;
    }

    public static String makeMd5(String source) {
        return MD5.getStringMD5(source);
    }

    public static final String filterUCS4(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }

        if (str.codePointCount(0, str.length()) == str.length()) {
            return str;
        }

        StringBuilder sb = new StringBuilder();

        int index = 0;
        while (index < str.length()) {
            int codePoint = str.codePointAt(index);
            index += Character.charCount(codePoint);
            if (Character.isSupplementaryCodePoint(codePoint)) {
                continue;
            }

            sb.appendCodePoint(codePoint);
        }

        return sb.toString();
    }

    /**
     * counter ASCII character as one, otherwise two
     *
     * @param str
     * @return count
     */
    public static int counterChars(String str) {
        // return
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            int tmp = (int) str.charAt(i);
            if (tmp > 0 && tmp < 127) {
                count += 1;
            } else {
                count += 2;
            }
        }
        return count;
    }


    /**
     *  * 必须包含数字、中文、字母
     *  * www.yoodb.com
     *  * @param str
     *  * @return
     *  
     */
    public static boolean isLetterDigit(String str) {
        boolean isDigit = false;
        boolean isLetter = false;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                isDigit = true;
            }
            if (Character.isLetter(str.charAt(i))) {
                isLetter = true;
            }
        }
        String regex = "^[a-zA-Z0-9]+$";
        boolean isRight = isDigit && isLetter && str.matches(regex);
        return isRight;
    }

    /**
     * 判断是否是存数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) str);
        return matcher.matches();
    }

    /**
     * 根据字符串获取当前首字母
     *
     * @param name
     * @return
     */
    public static String getLetter(String name) {
        String DefaultLetter = "#";
        if (TextUtils.isEmpty(name)) {
            return DefaultLetter;
        }
        char char0 = name.toLowerCase().charAt(0);
        if (Character.isDigit(char0)) {
            return DefaultLetter;
        }
        ArrayList<HanziToPinyin.Token> l = HanziToPinyin.getInstance().get(name.substring(0, 1));
        if (l != null && l.size() > 0 && l.get(0).target.length() > 0) {
            HanziToPinyin.Token token = l.get(0);
            // toLowerCase()返回小写， toUpperCase()返回大写
            String letter = token.target.substring(0, 1).toLowerCase();
            char c = letter.charAt(0);
            // 这里的 'a' 和 'z' 要和letter的大小写保持一直。
            if (c < 'a' || c > 'z') {
                return DefaultLetter;
            }
            return letter;
        }
        return DefaultLetter;
    }

    // 按字母顺序排序
    public static void sort(ArrayList<String> contactList) {

        Collections.sort(contactList, new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                if (str1.equals(str2)) {
                    return str1.compareTo(str2);
                } else {
                    if ("#".equals(str1)) {
                        return 1;
                    } else if ("#".equals(str2)) {
                        return -1;
                    }
                    return str1.compareTo(str2);
                }
            }
        });
    }
}
