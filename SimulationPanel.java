import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SimulationPanel extends JPanel {
    private final DrawPanel drawPanel;
    private final ThreadManager threadManager;
    private Thread gameThread;
    private volatile boolean running = false;
    private final FPSCounter fpsCounter = new FPSCounter();
    private int explorerSpawnX = -1; // Default invalid value
    private int explorerSpawnY = -1; // Default invalid value

    public SimulationPanel() {
        drawPanel = new DrawPanel();
        setLayout(new BorderLayout());
        add(drawPanel, BorderLayout.CENTER);
        threadManager = new ThreadManager();

        drawPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                drawPanel.removeComponentListener(this);
                startGameLoop();
            }
        });

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(threadManager.getExplorerController()); // Ensure the controller listens for key events
    }

    public void setExplorerSpawnLocation(int x, int y) {
        explorerSpawnX = x;
        explorerSpawnY = y;
    }

    public void startGameLoop() {
        threadManager.setCanvasSize(drawPanel.getWidth(), drawPanel.getHeight());
        running = true;
        gameThread = new Thread(this::gameLoop);
        gameThread.start();
    }

    private void gameLoop() {
        final long targetDelay = 1000 / 60; // Target delay for 60 FPS
        long lastFpsDisplayTime = System.currentTimeMillis();

        while (running) {
            long startTime = System.currentTimeMillis();
            fpsCounter.update();

            if (System.currentTimeMillis() - lastFpsDisplayTime >= 500 && fpsCounter.getFPS() != 0) {
                drawPanel.setFps(fpsCounter.getFPS());
                threadManager.checkAndAdjustThread();
                lastFpsDisplayTime = System.currentTimeMillis();
            }

            updateAndRepaint();
            threadManager.updateProcessingTimes();

            long sleepTime = targetDelay - (System.currentTimeMillis() - startTime);
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    running = false;
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void updateAndRepaint() {
        threadManager.updateParticles();
        threadManager.updateExplorer();
        SwingUtilities.invokeLater(drawPanel::repaint);
    }

    public void stopGameLoop() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public ThreadManager getThreadManager() {
        return threadManager;
    }

    public void refreshDisplay() {
        drawPanel.repaint();
    }

    private class DrawPanel extends JPanel {
        private double fpsToDisplay = 0;

        public void setFps(double fps) {
            this.fpsToDisplay = fps;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1280, 720);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        public DrawPanel() {
            super();
            setBackground(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int canvasHeight = getHeight();
            int canvasWidth = getWidth();

            g.setColor(Color.WHITE);

            if (threadManager.getExplorerCount() > 0) {
                Explorer explorer = threadManager.getExplorerController().getExplorer();
                if (explorer != null) {
                    // Calculate the viewport to center on the explorer
                    int explorerX = (int) explorer.getX();
                    int explorerY = (int) explorer.getY();

                    int peripheryWidth = 33 * 15; // 33 columns of pixels
                    int peripheryHeight = 19 * 15; // 19 rows of pixels

                    int leftX = explorerX - peripheryWidth / 2;
                    int topY = explorerY - peripheryHeight / 2;

                    // Ensure the viewport does not exceed canvas boundaries
                    if (leftX < 0) {
                        leftX = 0;
                    }
                    if (topY < 0) {
                        topY = 0;
                    }
                    if (leftX + peripheryWidth > canvasWidth) {
                        leftX = canvasWidth - peripheryWidth;
                    }
                    if (topY + peripheryHeight > canvasHeight) {
                        topY = canvasHeight - peripheryHeight;
                    }

                    g.setClip(leftX, topY, peripheryWidth, peripheryHeight); // Clip the drawing area to the viewport

                    // Draw particles within the periphery
                    threadManager.drawParticles(g, canvasHeight);

                    // Draw explorer
                    threadManager.drawExplorer(g, canvasHeight);

                    g.setClip(null); // Reset the clip area
                }
            } else {
                threadManager.drawParticles(g, canvasHeight);
            }

            if (fpsToDisplay >= 60) {
                g.setColor(Color.GREEN);
            } else if (fpsToDisplay >= 30) {
                g.setColor(Color.ORANGE);
            } else {
                g.setColor(Color.RED);
            }

            g.drawString(String.format("FPS: %.2f", fpsToDisplay), 10, 20);
            g.setColor(Color.BLUE);
            g.drawString(String.format("Number of Particles: %d", threadManager.getParticleCount()), 10, 40);
        }
    }
}
