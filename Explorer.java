import java.awt.Color;
import java.awt.Graphics;

public class Explorer {
    double x, y, vx, vy;

    public Explorer(int x, int y, double vx, double vy){
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }
    void update(int canvasWidth, int canvasHeight) {
        x += vx;
        y += vy;
        
        //IF not supposed to bounce
        if (x<=0){
            vx=0;
        }
        else if (x>=canvasWidth){
            vx = canvasWidth;
        }

        if (y<=0){
            vy=0;
        }
        else if (x>=canvasHeight){
            vy = canvasHeight;
        }
    }
    void draw(Graphics g, int canvasHeight) {
        int invertedY = canvasHeight - (int)y;
        g.setColor(Color.RED);
        g.fillRect((int)x, invertedY, 15, 15);
    }
    
    
    
}
