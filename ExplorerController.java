import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ExplorerController implements KeyListener {
    private Explorer explorer;
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    public ExplorerController() {
    }

    public void addExplorer(Explorer explorer) {
        this.explorer = explorer;
    }

    public void updateExplorer(int canvasWidth, int canvasHeight) {
        if (explorer != null) {
            if (upPressed) {
                explorer.setVelocity(0, 1);
            } else if (downPressed) {
                explorer.setVelocity(0, -1);
            } else if (leftPressed) {
                explorer.setVelocity(-1, 0);
            } else if (rightPressed) {
                explorer.setVelocity(1, 0);
            } else {
                explorer.setVelocity(0, 0);
            }
            explorer.update(canvasWidth, canvasHeight);
        }
    }

    public Explorer getExplorer() {
        return explorer;
    }

    public void drawExplorer(Graphics g, int canvasHeight) {
        if (explorer != null) {
            explorer.draw(g, canvasHeight);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (explorer != null) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    upPressed = true;
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    downPressed = true;
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    leftPressed = true;
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    rightPressed = true;
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (explorer != null) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    upPressed = false;
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    downPressed = false;
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    leftPressed = false;
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    rightPressed = false;
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
}
