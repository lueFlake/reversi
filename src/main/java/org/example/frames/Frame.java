package org.example.frames;

import java.util.List;

public abstract class Frame {
    protected List<String> lines;

    protected Frame(List<String> lines) {
        this.lines = lines;
    }

    public void show(int frameSize) {
        boolean error;
        do {
            try {
                printFrame(frameSize);
                process();
                error = false;
            } catch (RuntimeException exception) {
                addMessage(exception.getMessage());
                error = true;
            }
        } while (error);
    }

    private void printFrame(int frameSize) {
        for (String line : lines) {
            System.out.println(line);
        }
        for (int i = lines.size(); i < frameSize; i++) {
            System.out.println();
        }
    }

    protected abstract void process() throws RuntimeException;
    protected abstract void addMessage(String message);

    protected void clear() {
    }
}
