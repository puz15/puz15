import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import entities.PuzzleBoard;

public class GameRunner {
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private PuzzleBoard board = new PuzzleBoard(2);

    private static final int EXIT_INPUT = -2;

    public static void main(String[] args) throws IOException {
        GameRunner m = new GameRunner();
        println("Welcome to the amazing puzzle game");
        m.newGame();
        m.play();
    }

    private void play() throws IOException {
        while (!board.isSolved()) {
            println(board.toString());
            int input =
                    receiveInt("Enter which element you want to move. You can enter -1 to start a new game or -2 to exit");
            if (input == -1) {
                newGame();
                continue;
            }

            if (!board.moveSquareByValue(input)) {
                println("Invalid move, please try another one.");
            }

            if (board.isSolved()) {
                println("Puzzle solved, congrats! Another one?");
                newGame();
            }
        }

    }

    private void newGame() throws IOException {
        int sideLength = receiveInt("Please enter the required board side length (minimum 2), -2 to exit.", 2);
        board = new PuzzleBoard(sideLength);
        println(board.toString());
        board.initGame();
    }


    private int receiveInt(String message) throws IOException {
        return receiveInt(message,null);
    }

    private int receiveInt(String message, Integer min) throws IOException {
        while (true) {
            println("");
            println(message);
            try {
                String s = br.readLine();
                int input = Integer.parseInt(s);
                if (input == EXIT_INPUT) {
                    System.exit(1);
                }

                if (min == null || min.intValue() <= input) {
                    return input;
                }
            } catch (NumberFormatException e) {
                println("Please insert valid number");
                continue;
            }
        }
    }
    
    private static void println(String message) {
        System.out.println(message);
    }
}
