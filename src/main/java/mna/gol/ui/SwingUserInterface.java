package mna.gol.ui;

import lombok.RequiredArgsConstructor;
import mna.gol.GameOfLife;
import mna.gol.graphic.SwingRenderer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serial;
import javax.swing.*;

@RequiredArgsConstructor
public class SwingUserInterface extends JPanel implements KeyListener, UserInterface {
    @Serial
    private static final long serialVersionUID = -2559666472917571856L;

    private final transient GameOfLife gameOfLife;

    @Override
    public void createUI(int windowWidth, int windowHeight) {
        var frame = new JFrame("Game of Life");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.add(this);
        frame.setSize(windowWidth, windowHeight);
        frame.addKeyListener(this);
        frame.setFocusable(true);
        frame.requestFocus();

        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!gameOfLife.isRunning()) {
            new Thread(() -> {
                try {
                    gameOfLife.startGame(new SwingRenderer(this));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            gameOfLife.scheduleReset();
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // intentionally left empty
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // intentionally left empty
    }
}
