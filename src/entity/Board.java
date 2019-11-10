package entity;

/**
 * 游戏棋盘定义
 */
public class Board<T extends Block> {
    private T[][] boards;

    public T[][] getBoards() {
        return boards;
    }

    public void setBoards(T[][] boards) {
        this.boards = boards;
    }
}
