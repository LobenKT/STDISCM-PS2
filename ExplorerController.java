import java.awt.*;

public class ExplorerController {
    private Explorer explorer;
    public ExplorerController() {
    }
    public void addExplorer(Explorer explorer) {
       this.explorer=explorer;
    }    
    public void updateParticles(int canvasWidth, int canvasHeight) {
        explorer.update(canvasWidth, canvasHeight);
        
    }
    public Explorer getExplorer(){
        return explorer;
    }
    public void drawExplorer(Graphics g, int canvasHeight) {
            explorer.draw(g, canvasHeight);
        
    }
}
