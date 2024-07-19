import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ExplorerController implements KeyListener {
    private Explorer explorer;

    public void addExplorer(Explorer explorer) {
        this.explorer = explorer;
    }

    public void updateExplorer(int canvasWidth, int canvasHeight) {
        if (explorer != null) {
            explorer.update(canvasWidth, canvasHeight);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed for movement
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (explorer != null) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                    explorer.setVelocity(0, -1);
                    break;
                case KeyEvent.VK_A:
                    explorer.setVelocity(-1, 0);
                    break;
                case KeyEvent.VK_S:
                    explorer.setVelocity(0, 1);
                    break;
                case KeyEvent.VK_D:
                    explorer.setVelocity(1, 0);
                    break;
                case KeyEvent.VK_UP:
                    explorer.setVelocity(0, -1);
                    break;
                case KeyEvent.VK_LEFT:
                    explorer.setVelocity(-1, 0);
                    break;
                case KeyEvent.VK_DOWN:
                    explorer.setVelocity(0, 1);
                    break;
                case KeyEvent.VK_RIGHT:
                    explorer.setVelocity(1, 0);
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (explorer != null) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_A:
                case KeyEvent.VK_S:
                case KeyEvent.VK_D:
                case KeyEvent.VK_UP:
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_RIGHT:
                    explorer.setVelocity(0, 0);
                    break;
            }
        }
    }
}
