package org.example.frames;

import java.util.*;

public class MenuFrame extends Frame {
    private final String header;
    private ArrayList<String> menu;
    private boolean gotMessage;

    private int result;
    private boolean processed = false;

    public int getResult() {
        if (!processed) {
            throw new RuntimeException("Нет обработки");
        }
        return result;
    }

    public MenuFrame(ArrayList<String> menu, String header) {
        super(new ArrayList<>());
        gotMessage = false;
        this.menu = menu;
        this.header = header;
        lines.add(header);
        for (int i = 0; i < menu.size(); i++) {
            lines.add("%d. %s".formatted(i + 1, menu.get(i)));
        }
        lines.add("Введите номер опции.");
    }

    @Override
    protected void process() throws RuntimeException {
        processed = true;
        var scanner = new Scanner(System.in);
        try {
            result = scanner.nextInt();
        } catch (RuntimeException exception) {
            throw new RuntimeException("Ввод не является числом или слишком большой.");
        }
        if (result > menu.size()) {
            throw new RuntimeException("Номер превышает количество опций.");
        }
    }

    @Override
    protected void clear() {
        if (gotMessage) {
            lines.remove(lines.size() - 1);
            lines.remove(lines.size() - 1);
            gotMessage = false;
        }
    }

    @Override
    protected void addMessage(String message) {
        clear();
        lines.add("");
        lines.add(message);
        gotMessage = true;
    }
}
