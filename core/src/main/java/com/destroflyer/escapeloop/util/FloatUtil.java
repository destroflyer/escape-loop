package com.destroflyer.escapeloop.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

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
            decimalsText = "0" + decimalsText;
        }
        return integerValue + "." + decimalsText;
    }

    public static void lerp(Rectangle a, Rectangle b, float progress, Rectangle destination) {
        destination.set(
            MathUtils.lerp(a.x, b.x, progress),
            MathUtils.lerp(a.y, b.y, progress),
            MathUtils.lerp(a.width, b.width, progress),
            MathUtils.lerp(a.height, b.height, progress)
        );
    }
}
