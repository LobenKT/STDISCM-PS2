import java.util.List;

public class ExplorerEngine implements Runnable {
    private ExplorerController explorerController;
    private int canvasWidth, canvasHeight;
    private long lastProcessingTime = 0;

    
    public ExplorerEngine(int canvasWidth, int canvasHeight) {
        this.ExplorerController = new ExplorerController();
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public ExplorerEngine(int canvasWidth, int canvasHeight,Explorer exp) {
        this.explorerController = new ExplorerController();
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        explorerController.updateExplorer(canvasWidth, canvasHeight); // Assuming no walls to manage
        long endTime = System.currentTimeMillis();
        lastProcessingTime = endTime - startTime;
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

    
}
