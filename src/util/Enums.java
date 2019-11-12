package util;

import java.util.Random;

/**
 * 返回随机泛型值
 */
public class Enums {
    private static Random rand = new Random();

    public static <T extends Enum<T>> T random(Class<T> ec) {
        return random(ec.getEnumConstants());
    }

    public static <T> T random(T[] ts) {
        return ts[rand.nextInt(ts.length)];
    }
}
