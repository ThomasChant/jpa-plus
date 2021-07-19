package com.ct.condition.core;


public class StringUtil {

    public static String lowerFirst(String str) {
        if(str == null){
            return null;
        }
        if(str.length() > 0 ) {
            char ch = str.charAt(0);
            if (Character.isUpperCase(ch)) {
                return Character.toLowerCase(ch) + str.substring(1);
            }
        }
        return str;
    }

}
