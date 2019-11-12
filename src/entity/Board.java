package entity;

import util.SpecialBlockInfo;

import java.util.ArrayList;

/**
 * 游戏棋盘定义
 */
public class Board {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;

    private static Board board;

    private NormalBlockFactory blockFactory;

    private Block[][] blocks;

    //构造私有，单例设计
    private Board() {
        blockFactory = new NormalBlockFactory();

        blocks = new Block[WIDTH][HEIGHT];
        for (int i = 0; i < Board.WIDTH; i++) {
            for (int j = 0; j < Board.HEIGHT; j++) {
                blocks[i][j] = blockFactory.createNormalBlock("*");
            }
        }

        //读取特殊方块位置
        ArrayList<Integer> frozenIdx = SpecialBlockInfo.frozenIdx;
        for (int i = 0; i < frozenIdx.size(); i += 2) {
            int x = frozenIdx.get(i);
            int y = frozenIdx.get(i + 1);
            blocks[x][y] = new FrozenBlock(blocks[x][y].getElement());
        }

        ArrayList<Integer> viceIdx = SpecialBlockInfo.viceIdx;
        for (int i = 0; i < viceIdx.size(); i += 2) {
            int x = viceIdx.get(i);
            int y = viceIdx.get(i + 1);
            blocks[x][y] = new VineBlock(blocks[x][y].getElement());
        }
    }

    public Block[][] getBlocks() {
        return blocks;
    }

    public static Board getBoardInstance() {
        if (board == null) {
            board = new Board();
        }
        return board;
    }
}
