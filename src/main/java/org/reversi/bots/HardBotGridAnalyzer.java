package org.reversi.bots;

import org.reversi.models.GameGrid;

public class HardBotGridAnalyzer extends EasyBotGridAnalyzer {
    public HardBotGridAnalyzer(GameGrid grid) {
        super(grid);
    }

    @Override
    public int getEstimation(int x, int y) {
        if (!grid.validateCell(x, y)) {
            return 0;
        }
        return super.getEstimation(x, y) - getNextTurnEst(x, y);
    }

    private int getNextTurnEst(int x, int y) {
        grid.fill(x, y);
        int maxEstimation = 0;
        for (int i = 0; i < grid.getSize(); i++) {
            for (int j = 0; j < grid.getSize(); j++) {
                maxEstimation = Math.max(super.getEstimation(i, j), maxEstimation);
            }
        }
        grid.backToPreviousState();
        return maxEstimation;
    }
}
