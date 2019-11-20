package entity;

/**
 * 冰冻的方块
 * create by Zhouyang on 2019/11/11 15:56
 */
public class FrozenBlock extends Block {

    /**
     * 冰冻的级别，默认为2。等于0时为普通方块。
     */
    private int frozenLevel = 2;

    public FrozenBlock(Fruit fruit) {
        element = fruit;
    }

    public int getFrozenLevel() {
        return frozenLevel;
    }

    @Override
    public boolean canSelect() {
        return frozenLevel == 0;
    }

    @Override
    public void xiao() {
        frozenLevel--;
    }

    @Override
    public String toString() {
        return "Frozen{ " + element + " }";
    }
}
