package controller;

import entity.*;
import util.Enums;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

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
     * 交换两个方块
     */
    public void switchTwoBlock(Block block1, Block block2) {
        Block temp = new NormalBlock(Fruit.Apple);
        temp = block1;
        block1 = block2;
        block2 = temp;
    }

    public void xiaochuCheck(int x1, int y1, int x2, int y2) {
        Block[][] blocks = board.getBlocks();
        ArrayList<Location> readyToXiao = new ArrayList<>();
        Block block1 = blocks[x1][y1];
        Block block2 = blocks[x2][y2];

        if (!block1.canSelect() || !block2.canSelect()) {
            System.out.println("不能移动");
            return;
        }

        if (x1 == x2) {
            if (y1 < y2) {
                readyToXiao.addAll(rangeSearch(x1, y1, 'E'));
                readyToXiao.addAll(rangeSearch(x2, y2, 'W'));
            } else {
                readyToXiao.addAll(rangeSearch(x1, y1, 'W'));
                readyToXiao.addAll(rangeSearch(x2, y2, 'E'));
            }
        }

        else if (y1 == y2) {
            if (x1 < x2) {
                readyToXiao.addAll(rangeSearch(x2, y2, 'N'));
                readyToXiao.addAll(rangeSearch(x1, y1, 'S'));
            } else {
                readyToXiao.addAll(rangeSearch(x1, y1, 'N'));
                readyToXiao.addAll(rangeSearch(x2, y2, 'S'));
            }
        }

        //交换两个方块
        if (!readyToXiao.isEmpty()) {
            switchTwoBlock(block1, block2);
        }

        //消除方块
        for (Location location : readyToXiao) {
            blocks[location.getX()][location.getY()].xiao();
        }

        //难点：方块消除之后，上方方块下落，

    }

    private ArrayList<Location> rangeSearch(int x, int y, char direction) {
        Block[][] blocks = board.getBlocks();
        ArrayList<Location> readyToXiao = new ArrayList<>();
        Block block = null;

        if (direction == 'N') {
            block = blocks[x - 1][y];
        }
        if (direction == 'S') {
            block = blocks[x + 1][y];
        }
        if (direction == 'W') {
            block = blocks[x][y - 1];
        }
        if (direction == 'E') {
            block = blocks[x][y + 1];
        }

        if (direction == 'N' || direction == 'S') {
            int hStartIdx = -1;
            int hEndIdx = -1;
            int l = Math.max((y - 2), 0);

            for (int i = l; i <= y + 2 && i < Board.WIDTH; i++) {
                Block now = blocks[x][i];

                if (i == y) {
                    if (hStartIdx == -1) {
                        hStartIdx = i;
                    }
                    hEndIdx = i;
                }
                if (now .equals( block)) {
                    if (hStartIdx == -1) {
                        hStartIdx = i;
                    }
                    hEndIdx = i;
                }
                if (!now.equals( block) && hEndIdx - hStartIdx < 2) {
                    //过了中心，就不可能有3个相连了
                    if (i > y) {
                        break;
                    }
                    hStartIdx = -1;
                    hEndIdx = -1;
                }
            }

            if (hEndIdx - hStartIdx >= 2) {
                for (int i = hStartIdx; i <= hEndIdx ; i++) {
                    readyToXiao.add(new Location(x, i));
                }
            }

            int vStartIdx = x;
            int vEndIdx = -1;
            int iterStart = (direction == 'N') ? x + 1 : x - 1;
            int iterEnd = (direction == 'N') ? x + 2 : x - 2;

            for (int i = iterStart; ; ) {
                if (direction == 'N' && (i > iterEnd || i >= Board.HEIGHT)) {
                    break;
                }

                if (direction == 'S' && (i < iterEnd || i < 0)) {
                    break;
                }

                if (blocks[i][y] .equals( block )) {
                    vEndIdx = i;
                } else {
                    break;
                }

                i = (direction == 'N') ? i + 1 : i - 1;
            }
            if (vEndIdx - vStartIdx >= 2) {
                for (int i = vStartIdx + 1; i <= vEndIdx ; i++) {
                    readyToXiao.add(new Location(i, y));
                }
            }
        }
        else {
            int vStartIdx = -1;
            int vEndIdx = -1;
            int l = Math.max((x - 2), 0);

            for (int i = l; i <= x + 2 && i < Board.HEIGHT; i++) {
                Block now = blocks[i][y];

                if (i == x) {
                    if (vStartIdx == -1) {
                        vStartIdx = i;
                    }
                    vEndIdx = i;
                }
                if (now .equals( block)) {
                    if (vStartIdx == -1) {
                        vStartIdx = i;
                    }
                    vEndIdx = i;
                }
                if (!now .equals( block ) && vEndIdx - vStartIdx < 2) {
                    if (i > x) {
                        break;
                    }
                    vStartIdx = -1;
                    vEndIdx = -1;
                }

            }

            if (vEndIdx - vStartIdx >= 2) {
                for (int i = vStartIdx; i <= vEndIdx ; i++) {
                    readyToXiao.add(new Location(i, y));
                }
            }

            int hStartIdx = y;
            int hEndIdx = -1;
            int iterStart = (direction == 'W') ? y + 1 : y - 1;
            int iterEnd = (direction == 'E') ? y + 2 : y - 2;

            for (int i = iterStart; ; ) {
                if (direction == 'W' && (i > iterEnd || i >= Board.WIDTH)) {
                    break;
                }
                else if (direction == 'E' && (i < iterEnd || i < 0)) {
                    break;
                }


                if (blocks[x][i] .equals( block )) {
                    hEndIdx = i;
                } else {
                    break;
                }
                i = (direction == 'W') ? i + 1 : i - 1;
            }

            if (hEndIdx - hStartIdx >= 2) {
                for (int i = hStartIdx + 1; i <= hEndIdx ; i++) {
                    readyToXiao.add(new Location(x, i));
                }
            }
        }
        return readyToXiao;
    }

    public void getInput(int x1, int y1, int x2, int y2) {
        if (x1 == x2 && Math.abs(y1 - y2) == 1 ||
                y1 == y2 && Math.abs(x1 - x2) == 1) {
            //消除检测
            xiaochuCheck(x1, y1, x2, y2);

        } else {
            System.out.println("请选择相邻方块！");
        }
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
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String l1 = scanner.nextLine();
            String l2 = scanner.nextLine();
            String[] s1 = l1.split(",");
            String[] s2 = l2.split(",");
            game.getInput(Integer.parseInt(s1[0].trim()), Integer.parseInt(s1[1].trim()),
                    Integer.parseInt(s2[0].trim()), Integer.parseInt(s2[1].trim()));
        }
    }
}
