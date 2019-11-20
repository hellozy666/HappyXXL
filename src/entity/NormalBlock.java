package entity;

/**
 * create by Zhouyang on 2019/11/11 15:53
 */
public class NormalBlock extends Block{

    public NormalBlock(Fruit fruit) {
        element = fruit;
    }

    @Override
    public boolean canSelect() {
        return true;
    }

    @Override
    public String toString() {
        return "Normal{ " + element +
                " }";
    }
}
