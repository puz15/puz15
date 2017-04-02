package entities;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Class representing a puzzle game board
 */
public class PuzzleBoard {
    // a member which consists the current board
    private int[][] board;

    // a cache that allows to get the current location of a specific value easily.
    private BoardSquare[] valuesLocation;

    // the side length of the board.
    private int sideLength;

    // constant with the value representing a blank square
    private static final int BLANK_VALUE = 0;

    // constant with the number default pow of the side length in which we shuffle the board.
    private static final int DEFAULT_SHUFFLE_POW = 6;

    // the allowed move directions - 1 indicates row/col forward, -1 backwards.
    private static final int[][] allowedMoves = {{1, 0} ,{0, 1}, {-1, 0}, {0, -1}};

    public PuzzleBoard(int sideLength) {
        this.board = new int[sideLength][sideLength];
        this.valuesLocation = new BoardSquare[sideLength * sideLength];
        this.sideLength = sideLength;
    }

    public void initGame() {
        initBoard();
        shuffle(sideLength - 1, sideLength - 1, caluclateShuffleSteps());
    }

    /**
     * Method to shuffle a square with BLANK_VALUE to somewhere within the board. Performed by randomizing one of the
     * legit moves each time.
     *
     * @param zeroRow
     *            the row of the square with BLANK_VALUE
     * @param zeroCol
     *            the col of the square with BLANK_VALUE
     * @param shuffleSteps
     *            amount of steps to shuffle the square with the BLANK_VALUE.
     */
    public void shuffle(int zeroRow, int zeroCol, double shuffleSteps) {
        if (board[zeroCol][zeroRow] != BLANK_VALUE) {
            throw new UnsupportedOperationException("operation not supported for the given square");
        }

        Random r = new Random();
        do {
            for (double i = 0; i < shuffleSteps; i++) {
                final int currRowZero = zeroRow;
                final int currColZero = zeroCol;
                List<BoardSquare> possibleMoves = Arrays.stream(allowedMoves)
                        .map(move -> getSquare(currRowZero + move[0], currColZero + move[1]))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                int move = r.nextInt(possibleMoves.size());
                BoardSquare chosen = possibleMoves.get(move);
                switchSquares(chosen.getRow(), chosen.getCol(), zeroRow, zeroCol);
                zeroRow = chosen.getRow();
                zeroCol = chosen.getCol();
            }
        } while (isSolved());
    }

    /**
     * Fills the board with initial values between 0 and the board size - 1.
     */
    private void initBoard() {
        int curr = 1;
        for (int r = 0; r < sideLength; r++) {
            for (int c = 0; c < sideLength; c++) {
                board[c][r] = curr;
                if (curr < valuesLocation.length) {
                    valuesLocation[curr] = new BoardSquare(r, c);
                }
                curr++;
            }
        }
        board[sideLength - 1][sideLength - 1] = BLANK_VALUE;
        valuesLocation[BLANK_VALUE] = new BoardSquare(sideLength - 1, sideLength - 1);
    }

    private double caluclateShuffleSteps() {
        return Math.pow(sideLength, DEFAULT_SHUFFLE_POW);
    }

    /**
     * Moves the square with the given value in case it's adjacent to a square
     * with blank value.
     * @return boolean indicating whether the value was moved.
     */
    public boolean moveSquareByValue(int value) {
        BoardSquare square = valuesLocation[value];
        int row = square.getRow();
        int col = square.getCol();
        return moveSquareIfPossible(row, col);
    }

    /**
     * Moves a square to the specified destination if possible.
     */
    private boolean moveSquareIfPossible(int row, int col) {
        for (int[] move : allowedMoves) {
            int dstCol = col+move[1];
            int dstRow = row + move[0];
            if (canMoveSquare(row, col, dstRow, dstCol)) {
                switchSquares(row, col, dstRow, dstCol);
                return true;
            }
        }

        return false;
    }

    /**
     * Switches the values between two givens squares.
     */
    private void switchSquares(int row, int col, int dstRow, int dstCol) {
        int dstValue = board[dstCol][dstRow];
        BoardSquare dstSquare = valuesLocation[dstValue];
        int srcValue = board[col][row];
        BoardSquare srcSquare = valuesLocation[srcValue];

        valuesLocation[dstValue] = srcSquare;
        board[dstCol][dstRow] = srcValue;

        board[col][row] = dstValue;
        valuesLocation[srcValue] = dstSquare;
    }

    /**
     * Return true if the specified row/column are withing the boundaries of the board.
     * 
     * @param row
     * @param col
     * @return
     */
    private boolean isWithinRange(int row, int col) {
        return row > -1 && row < sideLength && col > -1 && col < sideLength;
    }

    /**
     * Returns boolean indicating whether the value in the specified row/column can be moved to the destination
     * row/column.
     * 
     * @param row
     *            source row
     * @param col
     *            source column
     * @param dstRow
     *            destination row
     * @param dstCol
     *            destination column
     * @return boolean
     */
    private boolean canMoveSquare(int row, int col, int dstRow, int dstCol) {
        if (!isWithinRange(dstRow, dstCol) || !isWithinRange(row, col)) {
            return false;
        }

        if (board[dstCol][dstRow] == BLANK_VALUE) {
            return true;
        }

        return false;
    }

    /**
     * Returns {@link BoardSquare} representing the location of the item in the specified location - to avoid creating
     * new objects constantly if the provided row/col are within the range, otherwise null.
     */
    private BoardSquare getSquare(int row, int col) {
        if (isWithinRange(row, col)) {
            return valuesLocation[board[col][row]];
        }
        return null;
    }

    /**
     * Example toString() representation of 3*3 board
     * 7	3	5
     *      2	4
     *  6	1	8
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int r = 0; r < sideLength; r++) {
            builder.append(System.lineSeparator());
            for (int c = 0; c < sideLength; c++) {
                int curr = board[c][r];
                builder.append(curr != BLANK_VALUE ? curr : "");
                builder.append("\t");
            }
        }
        builder.append(System.lineSeparator());
        return builder.toString();
    }

    /**
     * Returns boolean indicating whether the board is solved.
     */
    public boolean isSolved() {
        if (board[sideLength - 1][sideLength - 1] != BLANK_VALUE) {
            return false;
        }

        int counter = 1;
        for (int row = 0; row < sideLength; row++) {
            for (int col = 0; col < sideLength; col++) {
                if (board[col][row] != counter && !(row == sideLength - 1 && col == sideLength - 1)) {
                    return false;
                }
                counter++;
            }
        }

        return true;
    }
}
