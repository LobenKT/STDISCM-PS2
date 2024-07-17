import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SimulationPanel extends JPanel {
    private final DrawPanel drawPanel;
    private final ThreadManager threadManager;
    private Thread gameThread;
    private volatile boolean running = false;
    private FPSCounter fpsTracker = new FPSCounter();

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
    }

    public void startGameLoop() {
        threadManager.setCanvasSize(drawPanel.getWidth(), drawPanel.getHeight());
        running = true;
        gameThread = new Thread(this::gameLoop);
        gameThread.start();
    }

    private void gameLoop() {
        final long targetDelay = 1000 / 60; 
        long lastFpsDisplayTime = System.currentTimeMillis(); 

        while (running) {
            long now = System.currentTimeMillis();
            fpsTracker.update();
            if (now - lastFpsDisplayTime >= 500 && fpsTracker.getFPS() != 0) {
                drawPanel.setFps(fpsTracker.getFPS());
                threadManager.checkAndAdjustThread();
                lastFpsDisplayTime = now;
            }

            updateAndRepaint();
            threadManager.updateProcessingTimes();

            long sleepTime = targetDelay - (System.currentTimeMillis() - now);
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    running = false;
                }
            }
        }
    }

    private void updateAndRepaint() {
        threadManager.updateParticles();
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
        drawPanel.repaint(); // This will cause the DrawPanel to redraw and update the particle count display
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

            g.setColor(Color.WHITE);
            threadManager.drawParticles(g, canvasHeight);

            if (fpsToDisplay >= 60){
                g.setColor(Color.GREEN);
            } else if (fpsToDisplay >= 30){
                g.setColor(Color.ORANGE);
            } else {
                g.setColor(Color.RED);
            }
            
            g.drawString(String.format("FPS: %.2f", fpsToDisplay), 10, 20);
            g.setColor(Color.BLUE);
            g.drawString(String.format("Number of Particles: %d", threadManager.getParticleSize()), 100, 20);
        }
    }
}
