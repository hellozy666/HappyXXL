package entity;

/**
 * 被藤蔓缠绕的方块
 * create by Zhouyang on 2019/11/11 15:55
 */
public class VineBlock extends Block {

    /**
     * 表示该方块被缠绕
     */
    private boolean entwined = true;

    public VineBlock(Fruit fruit) {
        element = fruit;
    }

    @Override
    public boolean canSelect() {
        return entwined;
    }

    @Override
    public String toString() {
        return "VineBlock{ " + element + " }";
    }
}
