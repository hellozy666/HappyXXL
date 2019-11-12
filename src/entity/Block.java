package entity;

/**
 *  抽象方块类
 */
public abstract class Block {
    //水果元素
    public Fruit element;

    public Fruit getElement() {
        return element;
    }

    public void setElement(Fruit element) {
        this.element = element;
    }

    //该方框能否选中
    public abstract boolean canSelect();

    @Override
    public String toString() {
        return "Block{ " + element + "}";
    }
}
