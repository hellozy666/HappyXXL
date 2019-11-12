package util;

import java.util.ArrayList;

/**
 * 定义特殊方块的位置
 * create by Zhouyang on 2019/11/12 15:24
 */
public class SpecialBlockInfo {
    public static ArrayList<Integer> frozenIdx = new ArrayList<>();
    public static ArrayList<Integer> viceIdx = new ArrayList<>();

    static {
        frozenIdx.add(0);
        frozenIdx.add(0);

        frozenIdx.add(0);
        frozenIdx.add(1);

        frozenIdx.add(1);
        frozenIdx.add(0);

        frozenIdx.add(1);
        frozenIdx.add(1);

        frozenIdx.add(0);
        frozenIdx.add(9);

        frozenIdx.add(0);
        frozenIdx.add(8);

        frozenIdx.add(1);
        frozenIdx.add(8);

        frozenIdx.add(1);
        frozenIdx.add(9);

        frozenIdx.add(9);
        frozenIdx.add(0);

        frozenIdx.add(8);
        frozenIdx.add(0);

        frozenIdx.add(8);
        frozenIdx.add(1);

        frozenIdx.add(9);
        frozenIdx.add(1);

        frozenIdx.add(9);
        frozenIdx.add(9);

        frozenIdx.add(8);
        frozenIdx.add(8);

        frozenIdx.add(9);
        frozenIdx.add(8);

        frozenIdx.add(8);
        frozenIdx.add(9);

        viceIdx.add(4);
        viceIdx.add(4);

        viceIdx.add(4);
        viceIdx.add(5);

        viceIdx.add(5);
        viceIdx.add(4);

        viceIdx.add(5);
        viceIdx.add(5);
    }
}
