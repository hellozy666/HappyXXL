package entity;

import util.Enums;

/**
 *  方块
 */
public class Block {
    //水果元素
    private Fruit element;

    public Block() {
    }

    public Block(Fruit fruit) {
        element = fruit;
    }

    public Fruit getElement() {
        return element;
    }

    public void setElement(Fruit element) {
        this.element = element;
    }

    public static void main(String[] args) {
        Block block = new Block(Enums.random(Fruit.class));
        System.out.println(block.element);
    }
}
