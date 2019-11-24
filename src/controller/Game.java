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
    private void switchTwoBlock(Block block1, Block block2) {
        Fruit temp = Fruit.Apple;
        temp = block1.getElement();
        block1.setElement(block2.getElement());
        block2.setElement(temp);
    }

    private void xiaochu(HashMap<String, ArrayList<Location>> ... readyToXiao) {
        //消除方块
        Block[][] blocks = board.getBlocks();
        for (HashMap<String, ArrayList<Location>> map : readyToXiao) {
            map.forEach((key, value) -> value.forEach(location -> {
                blocks[location.getX()][location.getY()].xiao();
            }));
        }

        for (HashMap<String, ArrayList<Location>> map : readyToXiao) {
            //消除范围相关的变量
            int xbase;
            int xlen;
            int y0;
            int yn;

            if (!map.isEmpty()) {
                ArrayList<Location> height = map.get("height");
                ArrayList<Location> vertical = map.get("vertical");

                if (height != null && !height.isEmpty()) {
                    xlen = 1;
                    xbase = height.get(0).getX();
                    y0 = height.get(0).getY();
                    yn = height.get(height.size() - 1).getY();

                    fall(xbase, xlen, y0, yn);
                }

                if (vertical != null && !vertical.isEmpty()) {
                    xlen = vertical.size();
                    xbase = vertical.get(0).getX();
                    y0 = vertical.get(0).getY();
                    yn = y0;

                    //如果有 T 字型的消除，水平下沉之后，竖直方向下沉深度为 xlen - 1
                    if (map.size() == 2) {
                        fall(xbase, xlen - 1, y0, yn);
                    } else {
                        fall(xbase, xlen, y0, yn);
                    }
                }
            }
        }
    }

    //方块下沉
    private void fall(int xbase, int xlen, int y0, int yn) {
        Block[][] blocks = board.getBlocks();

        //现有方块下沉
        for (int i = xbase - 1; i >= 0 ; i--) {
            for (int j = y0; j <= yn; j++) {
                blocks[i + xlen][j].setElement(blocks[i][j].getElement());
            }
        }

        //空白补全
        for (int i = 0; i < xlen; i++) {
            for (int j = y0; j <= yn; j++) {
                blocks[i][j].setElement(Enums.random(Fruit.class));
            }
        }
    }

    public void xiaochuCheck(int x1, int y1, int x2, int y2) {
        Block[][] blocks = board.getBlocks();
        HashMap<String, ArrayList<Location>> firstXiao = null; //存放第一个块消除情况
        HashMap<String, ArrayList<Location>> secondXiao = null; //存放第二个块消除情况
        Location location1 = new Location(x1, y1);
        Location location2 = new Location(x2, y2);
        Block block1 = blocks[x1][y1];
        Block block2 = blocks[x2][y2];

        if (!block1.canSelect() || !block2.canSelect()) {
            System.out.println("不能移动");
            return;
        }

        if (x1 == x2) {
            if (y1 < y2) {
                firstXiao = rangeSearch(location1, location2, 'E');
                secondXiao = rangeSearch(location2, location1, 'W');
            } else {
                firstXiao = rangeSearch(location1, location2, 'W');
                secondXiao = rangeSearch(location2, location1, 'E');
            }
        }

        else if (y1 == y2) {
            if (x1 < x2) {
                firstXiao = rangeSearch(location1, location2, 'S');
                secondXiao = rangeSearch(location2, location1, 'N');
            } else {
                firstXiao = rangeSearch(location1, location2, 'N');
                secondXiao = rangeSearch(location2, location1, 'S');
            }
        }

        if (!firstXiao.isEmpty() || !secondXiao.isEmpty()) {
            //交换两个方块
            switchTwoBlock(block1, block2);
            //消除方块
            xiaochu(firstXiao, secondXiao);
        }
    }

    /**
     * 将 orginLocation 位置的方块放在 newLocation 位置时，能够消除的范围
     * @param originLocation 原方块位置
     * @param newLocation  新方块位置
     * @param direction   new方块 相对于 orgin方块 的方向
     * @return map
     */
    private HashMap<String, ArrayList<Location>> rangeSearch(Location originLocation,
                                                              Location newLocation, char direction) {
        Block[][] blocks = board.getBlocks();
        ArrayList<Location> hList = new ArrayList<>();  //水平方向待消除位置
        ArrayList<Location> vList = new ArrayList<>();  //垂直方向
        HashMap<String, ArrayList<Location>> readyToXiaoMap = new HashMap<>();//存储，横向和纵向
        Block block = blocks[originLocation.getX()][originLocation.getY()];

        int x = newLocation.getX();
        int y = newLocation.getY();

        if (direction == 'N' || direction == 'S') {
            int hStartIdx = -1;
            int hEndIdx = -1;
            int l = Math.max((y - 2), 0);

            for (int i = l; i <= y + 2 && i < Board.WIDTH; i++) {
                Block now = blocks[x][i];

                //由于是先判定，再交换。所以 i == y 也是方块相同
                if (i == y || now .equals( block)) {
                    if (hStartIdx == -1) {
                        hStartIdx = i;
                    }
                    hEndIdx = i;
                } else {
                    if (hEndIdx - hStartIdx < 2) {
                        //过了中心，就不可能有3个相连了
                        if (i > y) {
                            break;
                        }
                        hStartIdx = -1;
                        hEndIdx = -1;
                    } else {
                        break;
                    }
                }
            }

            if (hEndIdx - hStartIdx >= 2) {
                for (int i = hStartIdx; i <= hEndIdx ; i++) {
                    hList.add(new Location(x, i));
                }
                readyToXiaoMap.put("height", hList);
            }

            int vCount = 1;
            int iterStart = (direction == 'S') ? x + 1 : x - 2;
            int iterEnd = (direction == 'S') ? x + 2 : x - 1;

            for (int i = iterStart; i >= 0 && i < Board.HEIGHT && i <= iterEnd; i++ ) {
                if (blocks[i][y] .equals( block )) {
                    vCount ++;
                } else {
                    break;
                }
            }
            //能消的情况只能是3个相连
            if (vCount == 3) {
                for (int i = iterStart; i <= iterEnd ; i++) {
                    vList.add(new Location(i, y));
                }
                //保证从小到大的顺讯
                if (direction == 'S') {
                    vList.add(0, newLocation);
                } else {
                    vList.add(newLocation);
                }
                readyToXiaoMap.put("vertical", vList);
            }
        }
        else {
            int vStartIdx = -1;
            int vEndIdx = -1;
            int l = Math.max((x - 2), 0);

            for (int i = l; i <= x + 2 && i < Board.HEIGHT; i++) {
                Block now = blocks[i][y];

                if (i == x || now .equals( block)) {
                    if (vStartIdx == -1) {
                        vStartIdx = i;
                    }
                    vEndIdx = i;
                }
                else {
                    if (vEndIdx - vStartIdx < 2) {
                        if (i > x) {
                            break;
                        }
                        vStartIdx = -1;
                        vEndIdx = -1;
                    }
                    else {
                        break;
                    }
                }
            }

            if (vEndIdx - vStartIdx >= 2) {
                for (int i = vStartIdx; i <= vEndIdx ; i++) {
                    vList.add(new Location(i, y));
                }
                readyToXiaoMap.put("vertical", vList);
            }

            int hCount = 1;
            int iterStart = (direction == 'E') ? y + 1 : y - 2;
            int iterEnd = (direction == 'E') ? y + 2 : y - 1;

            for (int i = iterStart; i >= 0 && i < Board.WIDTH && i <= iterEnd; i++) {
                if (blocks[x][i] .equals( block )) {
                    hCount ++;
                } else {
                    break;
                }
            }

            if (hCount == 3) {
                for (int i = iterStart; i <= iterEnd ; i++) {
                    hList.add(new Location(x, i));
                }
                if (direction == 'E') {
                    hList.add(0, newLocation);
                } else {
                    hList.add(newLocation);
                }
                readyToXiaoMap.put("height", hList);
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

    private void showBoard() {
        Block[][] blocks = board.getBlocks();
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                System.out.print(blocks[i][j]+" ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Game game = new Game(Board.getBoardInstance());
        game.startCheck();

        game.showBoard();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String l1 = scanner.nextLine();
            String l2 = scanner.nextLine();
            String[] s1 = l1.split(",");
            String[] s2 = l2.split(",");
            game.getInput(Integer.parseInt(s1[0].trim()), Integer.parseInt(s1[1].trim()),
                    Integer.parseInt(s2[0].trim()), Integer.parseInt(s2[1].trim()));
            game.showBoard();
        }
    }
}
