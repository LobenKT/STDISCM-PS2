import java.awt.*;

public class Particle {
    private double x, y, vx, vy;

    public Particle(int x, int y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public void update(int canvasWidth, int canvasHeight) {
        x += vx;
        y += vy;

        // Bounce off the canvas edges
        if (x <= 0) {
            x = 0;
            vx *= -1;
        } else if (x >= canvasWidth) {
            x = canvasWidth;
            vx *= -1;
        }
        if (y <= 0) {
            y = 0;
            vy *= -1;
        } else if (y >= canvasHeight) {
            y = canvasHeight;
            vy *= -1;
        }
    }

    public void draw(Graphics g, int canvasHeight) {
        int invertedY = canvasHeight - (int) y - 10; // Adjust for particle size
        g.fillOval((int) x, invertedY, 10, 10);
    }
}
