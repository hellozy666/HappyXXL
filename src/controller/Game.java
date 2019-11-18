package controller;

import entity.Block;
import entity.Board;
import entity.Fruit;
import util.Enums;

/**
 * create by Zhouyang on 2019/11/11 16:41
 */
public class Game {

    private Board board;

    public Game(Board board) {
        this.board = board;
    }

    //初始化时，可能存在可以消除的方块，替换其中的方块，使其不能消除
    public void startCheck() {
        Block[][] blocks = board.getBlocks();
        //先保证每行不存在可以消的情况
        for (int i = 0; i < blocks.length; i++) {
            Block[] row = blocks[i];
            int cnt = 1;//相连重复计数

            //从第二个开始
            for (int j = 1; j < blocks.length; j++) {
                if (row[j].equals(row[j - 1])) {
                    cnt++;
                    if (cnt == 3) {
                        Fruit element = row[j].getElement();
                        row[j].setElement(Enums.expect(Fruit.class, element));
                        cnt = 1;
                    }
                } else {
                    cnt = 1;
                }
            }
        }

        //再保证列不存在可以消的情况
        for (int i = 0; i < blocks.length; i++) {
            int cnt = 1;
            for (int j = 1; j < blocks.length; j++) {
                Block block = blocks[j][i];
                if (block.equals(blocks[j - 1][i])) {
                    cnt++;

                    //此时需要替换改位置的方块，为了保证之前行不冲突，不能与行左右相邻两个元素相同
                    if (cnt == 3) {
                        //边界情况
                        Fruit leftFruit = null;
                        Fruit rightFruit = null;
                        if (i != 0) {
                            leftFruit = blocks[j][i - 1].getElement();
                        }

                        if (i != blocks.length - 1) {
                            rightFruit = blocks[j][i + 1].getElement();
                        }
                        blocks[j][i].setElement(
                                Enums.expect(Fruit.class, leftFruit, rightFruit, blocks[j][i].getElement()));
                        cnt = 1;
                    }
                } else {
                    cnt = 1;
                }
            }
        }
    }

    /**
     * 移动两个方块，判断是否消除
     */
    public void move() {

    }

    public static void main(String[] args) {
        Game game = new Game(Board.getBoardInstance());
        game.startCheck();
        Block[][] blocks = game.board.getBlocks();

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                System.out.print(blocks[i][j]+" ");
            }
            System.out.println();
        }
    }
}
