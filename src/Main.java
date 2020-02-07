/******************************************************************************

 Welcome to GDB Online.
 GDB online is an online compiler and debugger tool for C, C++, Python, Java, PHP, Ruby, Perl,
 C#, VB, Swift, Pascal, Fortran, Haskell, Objective-C, Assembly, HTML, CSS, JS, SQLite, Prolog.
 Code, Compile, Run and Debug online from anywhere in world.

 *******************************************************************************/
import com.sun.deploy.util.ArrayUtil;

import java.util.*;
import java.util.stream.Collectors;

public class Main
{
    private static final int ROWS = 3;
    private static final int COLS = 3;
    private static final boolean playerIsX = false;
    private static final boolean computerTriesToLose = true;

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Node root = loadNodes();

        Node selection = root;

        while (selection.getResult() == BOARD_VALUE.UNKNOWN) {

            if (playerIsX == !selection.isXTurn()) {
                selection = promptSelect(selection);
            } else {
                selection = autoSelect(selection);

                System.out.println("Computer played:");
                String[] newPosition = printPos(selection.getPos());
                Arrays.stream(newPosition).forEach(System.out::println);
            }
        }

        System.out.println("The result is: " + selection.getResult());
    }

    private static Node promptSelect(Node parent) {
        System.out.println("Please select one position from those below:\n");
        int posCount = parent.getChildren().size();
        String[][] printablePositions = new String[posCount][];

        String delimiter = "        ";

        for (int i=0; i<printablePositions.length; i++) {
            String[] printablePos = printPos(parent.getChildren().get(i).getPos());
            printablePositions[i] = addIndex(printablePos, i+"." + delimiter.substring(0, delimiter.length()/2-1));
        }

        String[] lines = joinLines(printablePositions, delimiter);
        Arrays.stream(lines).forEach(System.out::println);

        int choice = scanner.nextInt();

        return parent.getChildren().get(choice);
    }

    private static Node autoSelect(Node parent) {
        boolean isMaximize = computerTriesToLose ? playerIsX : !playerIsX;

        Optional<Node> select = isMaximize ?
                parent.getChildren().stream().max(Comparator.comparingInt(Node::getScore)) :
                parent.getChildren().stream().min(Comparator.comparingInt(Node::getScore));
        return select.get();
    }

    private static Node loadNodes() {
        Stack<Node> stack = new Stack<>();

        Node root = new Node(new Boolean[ROWS][COLS], false, 0, null);
        stack.push(root);

        while(!stack.isEmpty()) {
            Node parent = stack.pop();

            List<Node> children = parent.generateChildren();

            for (Node child : children) {
                BOARD_VALUE result = child.getBoardValue();

                if (result != BOARD_VALUE.UNKNOWN) {
                    child.setResult(result);
                    child.setScore(result == BOARD_VALUE.X_WIN ? 1 : result == BOARD_VALUE.DRAW ? 0 : -1);
                } else {
                    stack.push(child);
                }
            }
        }

        score(root);

        return root;
    }

    private static void score(Node node) {
        for (Node child : node.getChildren()) {
            score(child);
        }

        Node parent = node.getParent();

        if (parent == null || parent.getChildren().isEmpty()) {
            return;
        }

        boolean isMax = node.isXTurn();
        Optional<Integer> childrenScore = parent.getChildren().stream()
                .filter(n-> n.getScore() != null)
                .map(n -> n.getScore())
                .reduce((s1, s2) -> isMax ? Math.max(s1, s2) : Math.min(s1,s2));

        parent.setScore(childrenScore.get());
    }

    private static String[] printPos(Boolean[][] pos) {
        String[] lines = new String[pos.length];

        for (int i=0; i<lines.length; i++) {
            lines[i] = Arrays.stream(pos[i]).map(b -> getPrintableCharacter(b)).collect(Collectors.joining(" "));
        }

        return lines;
    }

    private static String[] addIndex(String[] lines, String index) {
        String[] result = new String[lines.length+1];
        result[0] = index;

        for (int i=0; i<lines.length; i++) {
            result [i+1] = lines[i];
        }

        return result;
    }

    private static String[] joinLines(String[][] lines, String delimiter) {
        if (lines.length == 0) {
            return null;
        }

        String[] result = new String[lines[0].length];

        List<String[][]> transposedBoards = new ArrayList<>();

        for (int i=0; i<lines.length; i++) {
            String[] board = lines[i];
            transposedBoards.add(transpose(board));
        }

        for (String[][] el : transposedBoards) {
            for (int i=0; i<result.length; i++) {
                if (result[i] == null) {
                    result[i] = el[i][0];
                } else {
                    result[i] += el[i][0];
                }

                result[i] += delimiter;
            }
        }

        return result;
    }

    private static String[][] transpose(String[] s) {
        String[][] result = new String[s.length][1];

        for (int i=0; i<s.length; i++) {
            result[i][0] = s[i];
        }

        return result;
    }

    private static String getPrintableCharacter(Boolean b) {
        if (b == null) return "_";
        return b ? "X" : "O";
    }
}
