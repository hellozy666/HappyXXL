package controller;

import entity.Block;
import entity.Board;

/**
 * create by Zhouyang on 2019/11/11 16:41
 */
public class Game {

    public Board board;

    public Game(Board board) {
        this.board = board;
    }

    public static void main(String[] args) {
        Game game = new Game(Board.getBoardInstance());
        Block[][] blocks = game.board.getBlocks();
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                System.out.print(blocks[i][j]+" ");
            }
            System.out.println();
        }
    }
}
