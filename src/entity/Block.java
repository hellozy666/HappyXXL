package entity;

import java.util.Objects;

/**
 *  抽象方块类
 */
public abstract class Block {
    //水果元素
    protected Fruit element;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Block block = (Block) o;
        return element == block.element;
    }

    @Override
    public int hashCode() {
        return Objects.hash(element);
    }
}
