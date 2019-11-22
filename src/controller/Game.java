package controller;

import entity.*;
import util.Enums;

import java.util.*;

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

    private void xiaochu(HashMap<String, ArrayList<Location>> ... readyToXiao) {
        //消除方块
        Block[][] blocks = board.getBlocks();
        for (HashMap<String, ArrayList<Location>> map : readyToXiao) {
            map.forEach((key, value) -> value.forEach(location -> {
                blocks[location.getX()][location.getY()].xiao();
            }));
        }
        
        //计算消除区域，用于下沉方块
        int xbase = Integer.MAX_VALUE,
                xlen = 0,
                y0 = -1,
                yn = -1,
                ybase = Integer.MAX_VALUE,
                ylen = 0,
                x0 = -1,
                xn = -1; //消除区域
        for (HashMap<String, ArrayList<Location>> map : readyToXiao) {
            if (!map.isEmpty()) {
                ArrayList<Location> height = map.get("height");
                ArrayList<Location> vertical = map.get("vertical");

                if (!height.isEmpty()) {
                    xlen++;
                    xbase = Math.min(height.get(0).getX(), xbase);

                    //y的顺序不定
                    int a = height.get(0).getY();
                    int b = height.get(height.size() - 1).getY();

                    y0 = Math.min(a, b);
                    yn = Math.max(a, b);
                }

                if (!vertical.isEmpty()) {
                    ylen++;
                    ybase = Math.min(vertical.get(0).getY(), ybase);

                    // x的顺序不定
                    int a = vertical.get(0).getX();
                    int b = vertical.get(vertical.size() - 1).getX();

                    x0 = Math.min(a, b);
                    xn = Math.max(a, b);
                }
            }
        }
        fall(xbase, xlen, y0, yn, ybase, ylen, x0, xn);
    }

    //方块下沉
    private void fall(int xbase, int xlen, int y0, int yn,
                      int ybase, int ylen, int x0, int xn) {
        Block[][] blocks = board.getBlocks();

        //水平
        for (int i = xbase - 1; i >= 0 ; i--) {
            for (int j = y0; j <= yn; j++) {
                blocks[i + xlen][j].setElement(blocks[i][j].getElement());
            }
        }

        //补全
        for (int i = 0; i < xlen; i++) {
            for (int j = y0; j <= yn; j++) {
                blocks[i][j].setElement(Enums.random(Fruit.class));
            }
        }

        //垂直
        //由于都是从上往下沉，所以这里ybase,ylen跟上面代表含义不同
        int len = xn - x0 + 1; //竖直高度
        for (int i = xn; i - len >= 0 ; i--) {
            for (int j = ybase; j < ybase + ylen  ; j++) {
                blocks[i][j].setElement(blocks[i - len][j].getElement());
            }
        }

        for (int i = 0; i < len; i++) {
            for (int j = ybase; j < ybase + ylen; j++) {
                blocks[i][j].setElement(Enums.random(Fruit.class));
            }
        }
    }

    public void xiaochuCheck(int x1, int y1, int x2, int y2) {
        Block[][] blocks = board.getBlocks();
        HashMap<String, ArrayList<Location>> firstXiao = null; //存放第一个块消除情况
        HashMap<String, ArrayList<Location>> secondXiao = null; //存放第二个块消除情况
        Block block1 = blocks[x1][y1];
        Block block2 = blocks[x2][y2];

        if (!block1.canSelect() || !block2.canSelect()) {
            System.out.println("不能移动");
            return;
        }

        if (x1 == x2) {
            if (y1 < y2) {
                firstXiao = rangeSearch(x1, y1, 'E');
                secondXiao = rangeSearch(x2, y2, 'W');
            } else {
                firstXiao = rangeSearch(x1, y1, 'W');
                secondXiao = rangeSearch(x2, y2, 'E');
            }
        }

        else if (y1 == y2) {
            if (x1 < x2) {
                firstXiao = rangeSearch(x2, y2, 'N');
                secondXiao = rangeSearch(x1, y1, 'S');
            } else {
                firstXiao = rangeSearch(x1, y1, 'N');
                secondXiao = rangeSearch(x2, y2, 'S');
            }
        }

        //交换两个方块
        if (!firstXiao.isEmpty() || !secondXiao.isEmpty()) {
            switchTwoBlock(block1, block2);
        }

        //消除方块
        if (!firstXiao.isEmpty() || !secondXiao.isEmpty()) {
            xiaochu(firstXiao, secondXiao);
        }

        //难点：方块消除之后，上方方块下落，

    }

    private HashMap<String, ArrayList<Location>> rangeSearch(int x, int y, char direction) {
        Block[][] blocks = board.getBlocks();
        ArrayList<Location> list = new ArrayList<>();
        HashMap<String, ArrayList<Location>> readyToXiaoMap = new HashMap<>();//存储，横向和纵向
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
                    list.add(new Location(x, i));
                }
                readyToXiaoMap.put("height", list);
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
                    list.add(new Location(i, y));
                }
                readyToXiaoMap.put("vertical", list);
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
                    list.add(new Location(i, y));
                }
                readyToXiaoMap.put("vertical", list);
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
                    list.add(new Location(x, i));
                }
                readyToXiaoMap.put("height", list);
            }
        }
        return readyToXiaoMap;
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
