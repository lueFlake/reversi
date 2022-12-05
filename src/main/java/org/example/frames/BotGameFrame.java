package org.example.frames;

import org.reversi.interfaces.GridAnalyzer;
import org.reversi.models.CellState;
import org.reversi.models.GameGrid;

import java.util.MissingFormatArgumentException;
import java.util.Objects;
import java.util.Scanner;

public class BotGameFrame extends PlayerGameFrame {
    protected final GridAnalyzer analyzer;
    private final CellState botColor;

    public BotGameFrame(GameGrid grid, GridAnalyzer analyzer, CellState botColor) {
        super(grid);
        if (botColor == CellState.Empty) {
            throw new MissingFormatArgumentException("Бот может играть за белых или черных");
        }
        this.botColor = botColor;
        this.analyzer = analyzer;
    }

    @Override
    protected void process() {
        if (grid.getCurrent() == botColor) {
            botProcess();
        } else {
            super.process();
        }
    }

    private void botProcess() {
        var scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (Objects.equals(input, "return")) {
            grid.backToPreviousState();
            addMessage("Возвращено в прошлое состояние");
            return;
        }
        var estimation = new int[grid.getSize()][grid.getSize()];
        int maxEstimation = 0;
        for (int i = 0; i < grid.getSize(); i++) {
            for (int j = 0; j < grid.getSize(); j++) {
                estimation[i][j] = analyzer.getEstimation(i, j);
                maxEstimation = Math.max(estimation[i][j], maxEstimation);
            }
        }
        for (int i = 0; i < grid.getSize(); i++) {
            for (int j = 0; j < grid.getSize(); j++) {
                if (estimation[i][j] == maxEstimation) {
                    grid.fill(i, j);
                    return;
                }
            }
        }
    }
}
