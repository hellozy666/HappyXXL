package entity;

import util.Enums;

/**
 * 创建Block的简单工厂
 * create by Zhouyang on 2019/11/12 14:41
 */
public class NormalBlockFactory {

    //创建指定fruit的普通方块，或者随机
    public Block createNormalBlock(String type) {
        Block block = null;
        try {
             block = new NormalBlock(Fruit.valueOf(type));
        } catch (IllegalArgumentException e) {
            block = new NormalBlock(Enums.random(Fruit.class));
        }

        return block;
    }
}
