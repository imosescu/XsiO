import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node {

    private Boolean[][] pos;
    private boolean isXTurn;
    private Node parent;
    private List<Node> children = new ArrayList<>();
    private int level;
    private BOARD_VALUE result = BOARD_VALUE.UNKNOWN;
    private Integer score;

    public Node(Boolean[][] pos, boolean isXTurn, int level, Node parent) {
        this.pos = pos;
        this.isXTurn = isXTurn;
        this.level = level;
        this.parent = parent;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public boolean isLastLevel() {
        return level == (pos.length * pos[0].length);
    }

    public List<Node> getChildren() {
        return children;
    }

    public int getNextLevel() {
        return level + 1;
    }

    public Boolean[][] getPos() {
        return pos;
    }

    public boolean isXTurn() {
        return isXTurn;
    }

    public int getLevel() {
        return level;
    }

    public BOARD_VALUE getResult() {
        return result;
    }

    public void setResult(BOARD_VALUE result) {
        this.result = result;
    }

    public Node getParent() {
        return parent;
    }

    public List<Node> generateChildren() {
        for (int i=0; i<pos.length; i++) {
            for (int j=0; j<pos.length; j++) {
                if (pos[i][j] == null) {
                    Boolean[][] copy = Arrays.stream(getPos()).map(Boolean[]::clone).toArray(Boolean[][]::new);
                    copy[i][j] = !isXTurn;

                    Node child = new Node(copy, !isXTurn, getNextLevel(), this);
                    children.add(child);
                }
            }
        }

        return children;
    }

    public BOARD_VALUE getBoardValue() {
        Boolean[] mainDiagonal = new Boolean[pos.length];
        Boolean[] inverseDiagonal = new Boolean[pos.length];
        Boolean[][] columns = new Boolean[pos.length][pos.length];

        for (int i=0; i<pos.length; i++) {
            if (isMultiEqual(pos[i])) return pos[i][0] ? BOARD_VALUE.X_WIN : BOARD_VALUE.O_WIN;

            for (int j=0; j<pos[i].length; j++) {
                columns[j][i] = pos[i][j];

                if (i==j) {
                    mainDiagonal[i] = pos[i][j];
                }

                if (i+j == pos.length-1) {
                    inverseDiagonal[i] = pos[i][j];
                }
            }
        }

        if (isMultiEqual(mainDiagonal)) return mainDiagonal[0] ? BOARD_VALUE.X_WIN : BOARD_VALUE.O_WIN;
        if (isMultiEqual(inverseDiagonal)) return inverseDiagonal[0] ? BOARD_VALUE.X_WIN : BOARD_VALUE.O_WIN;

        for (Boolean[] column : columns) {
            if (isMultiEqual(column)) return column[0] ? BOARD_VALUE.X_WIN : BOARD_VALUE.O_WIN;
        }

        if (isLastLevel()) return BOARD_VALUE.DRAW;

        return BOARD_VALUE.UNKNOWN;
    }

    private boolean isEqual (Boolean b1, Boolean b2) {
        if (b1 == null || b2 == null ) {
            return false;
        }

        return b1.equals(b2);
    }

    private boolean isMultiEqual(Boolean[] booleans) {
        for (int i=0; i<booleans.length-1; i++) {
            if (!isEqual(booleans[i], booleans[i+1])) return false;
        }
        return true;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
