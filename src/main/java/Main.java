import java.util.*;
import java.util.stream.Collectors;

public class Main
{
    private static final int ROWS = 3;
    private static final int COLS = 3;
    private static boolean playerIsX = true;
    private static boolean isTwoPlayers = false;
    private static boolean computerTriesToLose = false;

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if(args.length == 3) {
            playerIsX = Boolean.parseBoolean(args[0]);
            isTwoPlayers = Boolean.parseBoolean(args[1]);
            computerTriesToLose = Boolean.parseBoolean(args[2]);
        }

        Node selection = loadNodes();

        while (selection.getResult() == BOARD_VALUE.UNKNOWN) {

            if (playerIsX == !selection.isXTurn() || isTwoPlayers) {
                selection = promptSelect(selection);
            } else {
                selection = autoSelect(selection);

                System.out.println("Computer played:");
                printPos(getPrintablePos(selection.getPos()));
            }
        }

        System.out.println("----------RESULT----------");
        printPos(getPrintablePos(selection.getPos()));
        System.out.println("The result is: " + selection.getResult());
    }

    private static Node promptSelect(Node parent) {
        System.out.println("Please select one position from those below:\n");
        int posCount = parent.getChildren().size();
        String[][] printablePositions = new String[posCount][];

        String delimiter = "        ";

        for (int i=0; i<printablePositions.length; i++) {
            String[] printablePos = getPrintablePos(parent.getChildren().get(i).getPos());
            printablePositions[i] = addIndex(printablePos, i+"." + delimiter.substring(0, delimiter.length()/2-1));
        }

        String[] lines = joinLines(printablePositions, delimiter);
        printPos(lines);

        int choice = scanner.nextInt();

        return parent.getChildren().get(choice);
    }

    private static Node autoSelect(Node parent) {
        boolean isMaximize = (playerIsX == computerTriesToLose);

        Optional<Node> select = isMaximize ?
                parent.getChildren().stream().max(Comparator.comparingInt(Node::getScore)) :
                parent.getChildren().stream().min(Comparator.comparingInt(Node::getScore));

        return select.orElseThrow(() -> new RuntimeException("No element found"));
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
                .map(Node::getScore)
                .reduce((s1, s2) -> isMax ? Math.max(s1, s2) : Math.min(s1,s2));

        childrenScore.ifPresent(parent::setScore);
    }

    private static String[] getPrintablePos(Boolean[][] pos) {
        String[] lines = new String[pos.length];

        for (int i=0; i<lines.length; i++) {
            lines[i] = Arrays.stream(pos[i]).map(Main::getPrintableCharacter).collect(Collectors.joining(" "));
        }

        return lines;
    }

    private static void printPos(String[] pos) {
        Arrays.stream(Optional.ofNullable(pos).orElseThrow(() -> new RuntimeException("No position to print"))).forEach(System.out::println);
    }

    private static String[] addIndex(String[] lines, String index) {
        String[] result = new String[lines.length+1];
        result[0] = index;

        System.arraycopy(lines, 0, result, 1, lines.length);

        return result;
    }

    private static String[] joinLines(String[][] lines, String delimiter) {
        if (lines.length == 0) {
            return null;
        }

        String[] result = new String[lines[0].length];

        List<String[][]> transposedBoards = new ArrayList<>();

        for (String[] board : lines) {
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
