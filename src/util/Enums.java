package util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/**
 * 返回随机泛型值
 */
public class Enums {
    private static Random rand = new Random();

    //返回除array之外的随机泛型值
    public static <T extends Enum<T>> T expect(Class<T> ec, T... array) {
        HashSet<T> set = new HashSet<>(Arrays.asList(array));
        T[] ts = ec.getEnumConstants();
        T t = ts[rand.nextInt(ts.length)];

        while (set.contains(t)) {
            t = ts[rand.nextInt(ts.length)];
        }
        return t;
    }

    //返回随机泛型值
    public static <T extends Enum<T>> T random(Class<T> ec) {
        return random(ec.getEnumConstants());
    }

    public static <T> T random(T[] ts) {
        return ts[rand.nextInt(ts.length)];
    }
}
