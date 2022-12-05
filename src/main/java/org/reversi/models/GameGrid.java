package org.reversi.models;

import org.reversi.interfaces.Persistant;

import java.util.*;

public class GameGrid implements Persistant {
    private final CellState[][] cells;
    private final boolean[][] validation;
    private boolean whiteTurn;
    private final int gridSide;
    private boolean changed;

    private final List<GridUpdate> updates;
    private int globalUpdates;

    private static class GridUpdate {
        int x;
        int y;
        CellState state;
        CellState prevState;
        int globalUpdateId;

        public GridUpdate(int x, int y, CellState state, CellState prevState, int globalUpdateId) {
            this.x = x;
            this.y = y;
            this.state = state;
            this.prevState = prevState;
            this.globalUpdateId = globalUpdateId;
        }
    }

    public GameGrid(int gridSide, CellState firstTurnBy) {
        this.gridSide = gridSide;
        updates = new ArrayList<>();
        validation = new boolean[gridSide][gridSide];
        cells = new CellState[gridSide][gridSide];
        globalUpdates = 0;
        whiteTurn = (firstTurnBy == CellState.White);
        initialize();
    }

    private void setValidation() {
        changed = false;
        for (int i = 0; i < gridSide; i++) {
            for (int j = 0; j < gridSide; j++) {
                validation[i][j] = setupCellValidation(i, j);
            }
        }
    }

    private boolean setupCellValidation(int x, int y) {
        if (cells[x][y] != CellState.Empty) {
            return false;
        }
        boolean cond = false;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                cond = cond || checkLine(x, y, i, j);
            }
        }
        return cond;
    }

    public boolean validateCell(int x, int y) {
        if (changed) {
            setValidation();
        }
        return validation[x][y];
    }

    private boolean checkLine(int x1, int y1, int dx, int dy) {
        int i = x1 + dx, j = y1 + dy;
        int it = 0;
        while (i < gridSide && i >= 0 && j < gridSide && j >= 0 && cells[i][j] == getOpponent()) {
            i += dx;
            j += dy;
            it++;
        }
        if (i == gridSide || j == gridSide || i < 0 || j < 0 || it < 1) {
            return false;
        }
        return cells[i][j] == getCurrent();
    }

    private void paintLine(int x1, int y1, int dx, int dy) {
        int i = x1 + dx, j = y1 + dy;
        while (i < gridSide && i > 0 && j < gridSide && j > 0 && cells[i][j] == getOpponent()) {
            set(i, j, getCurrent());
            i += dx;
            j += dy;
        }
    }

    private void set(int x, int y, CellState state) {
        updates.add(new GridUpdate(x, y, state, cells[x][y], globalUpdates));
        cells[x][y] = state;
    }

    public void fill(int x, int y) throws RuntimeException {
        changed = true;
        if (!(x >= 0 && x < gridSide && y >= 0 && y < gridSide) || !validateCell(x, y)) {
            throw new RuntimeException("Невозможно закрасить клетку.");
        }
        globalUpdates++;
        set(x, y, getCurrent());
        paintFrom(x, y);
    }

    private void paintFrom(int x, int y) {

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (!(i == 0 && j == 0) && checkLine(x, y, i, j)) {
                    paintLine(x, y, i, j);
                }
            }
        }
    }

    public void changeTurn() {
        whiteTurn = !whiteTurn;
        setValidation();
    }

    public CellState get(int x, int y) {
        return cells[x][y];
    }

    public CellState getCurrent() {
        return whiteTurn ? CellState.White : CellState.Black;
    }

    public CellState getOpponent() {
        return !whiteTurn ? CellState.White : CellState.Black;
    }

    public int getSize() {
        return gridSide;
    }

    public boolean isPlayable() {
        for (int i = 0; i < gridSide; i++) {
            for (int j = 0; j < gridSide; j++) {
                if (validateCell(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getScore(CellState state) {
        if (state == CellState.Empty) {
            return 0;
        }
        int score = 0;
        for (int i = 0; i < gridSide; i++) {
            for (int j = 0; j < gridSide; j++) {
                score += cells[i][j] == state ? 1 : 0;
            }
        }
        return score;
    }

    public void initialize() {
        changed = true;
        for (int i = 0; i < gridSide; i++) {
            for (int j = 0; j < gridSide; j++) {
                cells[i][j] = CellState.Empty;
            }
        }
        int midHighRight = gridSide / 2;
        cells[midHighRight][midHighRight] = CellState.White;
        cells[midHighRight][midHighRight - 1] = CellState.Black;
        cells[midHighRight - 1][midHighRight - 1] = CellState.White;
        cells[midHighRight - 1][midHighRight] = CellState.Black;
    }

    @Override
    public void backToPreviousState() {
        changed = true;
        while (updates.size() > 0 && updates.get(updates.size() - 1).globalUpdateId == globalUpdates) {
            var upd = updates.get(updates.size() - 1);
            cells[upd.x][upd.y] = upd.prevState;
            updates.remove(updates.size() - 1);
        }
        changeTurn();
        globalUpdates--;
    }
}
