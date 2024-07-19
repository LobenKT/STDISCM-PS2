import java.util.List;

public class ExplorerEngine implements Runnable {
    private ExplorerController explorerController;
    private int canvasWidth, canvasHeight;
    private long lastProcessingTime = 0;
    private volatile boolean running = false; // Initially not running, only runs when activated

    public ExplorerEngine(int canvasWidth, int canvasHeight) {
        this.explorerController = new ExplorerController();
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public ExplorerEngine(int canvasWidth, int canvasHeight, Explorer explorer) {
        this(canvasWidth, canvasHeight); // Call the other constructor
        addExplorer(explorer);
    }

    @Override
    public void run() {
        while (running) {
            long startTime = System.currentTimeMillis();
            explorerController.updateExplorer(canvasWidth, canvasHeight); // Assuming updates for explorer
            long endTime = System.currentTimeMillis();
            lastProcessingTime = endTime - startTime;

            try {
                Thread.sleep(16); // Aim for roughly 60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; // Exit the loop on interruption
            }
        }
    }

    public void addExplorer(Explorer explorer) {
        explorerController.addExplorer(explorer);
    }

    public ExplorerController getExplorerController() {
        return explorerController;
    }

    public long getLastProcessingTime() {
        return lastProcessingTime;
    }

    public void pause() {
        running = false; // Pause the thread
    }

    public void resume() {
        if (!running) {
            running = true;
            Thread runner = new Thread(this);
            runner.start(); // Resume the thread
        }
    }

    public void stop() {
        running = false; // Provide a method to stop the engine
    }
}
