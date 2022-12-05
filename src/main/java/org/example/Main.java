package org.example;

import org.example.frames.BotGameFrame;
import org.example.frames.MenuFrame;
import org.example.frames.PlayerGameFrame;
import org.reversi.bots.HardBotGridAnalyzer;
import org.reversi.interfaces.GridAnalyzer;
import org.reversi.models.CellState;
import org.reversi.models.GameGrid;
import org.reversi.bots.EasyBotGridAnalyzer;

import java.util.ArrayList;

public class Main {
    private static int frameSize = 12;
    private static int gridSize = 8;

    private static GridAnalyzer hintAnalyzer = null;

    private static final CellState botColor = CellState.White;

    public static void main(String[] args) {
        var startList = new ArrayList<String>();
        startList.add("Игра против компьютера.");
        startList.add("Игра против игрока.");
        startList.add("Настройки подсказок.");
        startList.add("Выход.");
        var startMenu = new MenuFrame(startList, "Реверсио!");
        int gameOption;
        boolean end = false;
        do {
            startMenu.show(frameSize);
            gameOption = startMenu.getResult();

            var grid = new GameGrid(gridSize, CellState.Black);

            switch (gameOption) {
                case 1 -> {
                    GridAnalyzer analyzer = chooseDifficulty(grid);
                    var pveGameFrame = new BotGameFrame(grid, analyzer, botColor);
                    if (hintAnalyzer != null) {
                        pveGameFrame.enableHints(hintAnalyzer);
                    }
                    end = startGame(pveGameFrame);
                }
                case 2 -> {
                    var pvpGameFrame = new PlayerGameFrame(grid);
                    if (hintAnalyzer != null) {
                        pvpGameFrame.enableHints(hintAnalyzer);
                    }
                    end = startGame(pvpGameFrame);
                }
                case 3 -> hintAnalyzer = chooseSettings(grid);
            }
        } while (gameOption != startList.size() && !end);
    }

    private static GridAnalyzer chooseSettings(GameGrid grid) {
        var options = new ArrayList<String>();
        options.add("Выбрать простые подсказки.");
        options.add("Выбрать продвинутые подсказки.");
        options.add("Без подсказок.");
        var menu = new MenuFrame(options, "Настройки.");
        menu.show(frameSize);
        switch (menu.getResult()) {
            case 1 -> {
                return new EasyBotGridAnalyzer(grid);
            }
            case 2 -> {
                return new HardBotGridAnalyzer(grid);
            }
            case 3 -> {
                return null;
            }
        }
        return null;
    }

    private static GridAnalyzer chooseDifficulty(GameGrid grid) {
        var options = new ArrayList<String>();
        options.add("Легко.");
        options.add("Сложно.");
        var menu = new MenuFrame(options, "Игра против компьютера.");
        menu.show(frameSize);
        switch (menu.getResult()) {
            case 1 -> {
                return new EasyBotGridAnalyzer(grid);
            }
            case 2 -> {
                return new HardBotGridAnalyzer(grid);
            }
        }
        return null;
    }

    private static boolean startGame(PlayerGameFrame frame) {
        while (frame.getGrid().isPlayable()) {
            frame.show(frameSize);
            frame.getGrid().changeTurn();
            frame.refreshGrid();
        }
        return finalizeGame(frame.getGrid());
    }

    private static boolean finalizeGame(GameGrid grid) {
        var finalMenu = new ArrayList<String>();
        finalMenu.add("Выход.");
        finalMenu.add("В главное меню.");
        int whiteScore = grid.getScore(CellState.White);
        int blackScore = grid.getScore(CellState.Black);

        String text = (whiteScore > blackScore ? "Выйграли белые." : "Выйграли черные.") +
                "Счет:" +
                whiteScore +
                ":" +
                blackScore;

        var finalFrame = new MenuFrame(finalMenu, text);
        finalFrame.show(frameSize);
        int option = finalFrame.getResult();
        return option != 2;
    }
}