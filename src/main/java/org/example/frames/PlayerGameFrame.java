package org.example.frames;

import org.reversi.interfaces.GridAnalyzer;
import org.reversi.models.CellState;
import org.reversi.models.GameGrid;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class PlayerGameFrame extends Frame {

    protected GameGrid grid;
    protected boolean gotMessage;

    private static final Character black = '●';
    private static final Character white = '◯';
    private static final Character empty = '.';
    private static final Character valid = '◎';
    private static final Character hint = '*';

    private GridAnalyzer hinter = null;

    public PlayerGameFrame(GameGrid grid) {
        super(new ArrayList<>());
        this.grid = grid;
        refreshGrid();
        gotMessage = false;
    }

    @Override
    protected void process() throws RuntimeException {
        var scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (Objects.equals(input, "return")) {
            grid.backToPreviousState();
            addMessage("Возвращено в прошлое состояние");
            return;
        }
        Scanner secondary = new Scanner(input);
        int x = secondary.nextInt();
        int y = secondary.nextInt();
        grid.fill(x, y);
    }

    @Override
    protected void addMessage(String message) {
        refreshGrid();
        lines.add("");
        lines.add(message);
        gotMessage = true;
    }

    public void refreshGrid() {
        lines.clear();

        int max = hinter == null ? 0 : hinter.getMax();
        for (int i = 0; i < grid.getSize(); i++) {
            var line = new StringBuilder();
            for (int j = 0; j < grid.getSize(); j++) {
                if (grid.get(i, j) == CellState.White) {
                    line.append(white);
                } else if (grid.get(i, j) == CellState.Black) {
                    line.append(black);
                } else {
                    if (grid.validateCell(i, j)) {
                        if (hinter != null && hinter.getEstimation(i, j) == max) {
                            line.append(hint);
                        } else {
                            line.append(valid);
                        }
                    } else {
                        line.append(empty);
                    }
                }
                line.append(' ');
            }
            lines.add(line.toString());
        }
        gotMessage = false;
    }

    @Override
    protected void clear() {
        clearError();
        grid.initialize();
    }

    private void clearError() {
        if (gotMessage) {
            lines.remove(lines.size() - 1);
            lines.remove(lines.size() - 1);
            gotMessage = false;
        }
    }

    public GameGrid getGrid() {
        return grid;
    }

    public void enableHints(GridAnalyzer analyzer) {
        this.hinter = analyzer;
        refreshGrid();
    }
}
