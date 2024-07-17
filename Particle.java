import java.awt.*;

public class Particle {
    double x, y, vx, vy;

    public Particle(int x, int y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    void update(int canvasWidth, int canvasHeight) {
        x += vx;
        y += vy;

        // Bounce off the canvas edges
        if (x <= 0 || x >= canvasWidth) {
            vx *= -1;
        }
        if (y <= 0 || y >= canvasHeight) {
            vy *= -1;
        }
    }

    void draw(Graphics g, int canvasHeight) {
        int invertedY = canvasHeight - (int)y;
        g.fillOval((int)x, invertedY, 10, 10);
    }
}
