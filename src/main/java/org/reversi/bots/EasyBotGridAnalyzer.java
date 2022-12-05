package org.reversi.bots;

import org.reversi.interfaces.GridAnalyzer;
import org.reversi.models.GameGrid;

public class EasyBotGridAnalyzer implements GridAnalyzer {

    protected final GameGrid grid;

    public EasyBotGridAnalyzer(GameGrid grid) {
        this.grid = grid;
    }

    @Override
    public int getEstimation(int x, int y) {
        if (!grid.validateCell(x, y)) {
            return 0;
        }
        int result = 4 * getPositionValue(x, y);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                result += getClosingPositionValue(x, y, i, j);
            }
        }
        return result;
    }

    @Override
    public int getMax() {
        int max = 0;
        for (int i = 0; i < grid.getSize(); i++) {
            for (int j = 0; j < grid.getSize(); j++) {
                max = Math.max(max, getEstimation(i, j));
            }
        }
        return max;
    }

    protected int getPositionValue(int x, int y) {
        int value = 0;
        value += x == 0 || x == grid.getSize() - 1 ? 1 : 0;
        value += y == 0 || y == grid.getSize() - 1 ? 1 : 0;
        return value;
    }

    protected int getClosingPositionValue(int x, int y, int dx, int dy) {
        int i = x + dx, j = y + dy, it = 0;
        while (checkBorders(i, j) && grid.get(i, j) == grid.getOpponent()) {
            i += dx;
            j += dy;
            it++;
        }
        if (!checkBorders(i, j) || it < 1) {
            return 0;
        }
        return 10 * (Math.min(1, getPositionValue(i, j)) + 1);
    }

    private boolean checkBorders(int i, int j) {
        return i < grid.getSize() && i >= 0 && j < grid.getSize() && j >= 0;
    }
}
