package com.destroflyer.escapeloop.util;

public class FloatUtil {

    public static String format(float value, int decimals) {
        int base = (int) Math.pow(10, decimals);
        int integerValue = (int) value;
        int decimalsValue = Math.round((value % 1) * base);
        if (decimalsValue == base) {
            integerValue++;
            decimalsValue = 0;
        }
        String decimalsText = "" + decimalsValue;
        while (decimalsText.length() < decimals) {
            decimalsText += "0";
        }
        return integerValue + "." + decimalsText;
    }
}
