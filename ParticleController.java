import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.awt.*;

public class ParticleController {
    private List<Particle> particles = new CopyOnWriteArrayList<>();

    public ParticleController() {
    }

    public ParticleController(List<Particle> particles) {
        this.particles = new CopyOnWriteArrayList<>(particles);
    }    

    public void addParticle(Particle particle) {
        particles.add(particle);
    }

    public void updateParticles(int canvasWidth, int canvasHeight) {
        for (Particle particle : particles) {
            particle.update(canvasWidth, canvasHeight);
        }
    }

    public List<Particle> getParticles(){
        return particles;
    }

    public void drawParticles(Graphics g, int canvasHeight) {
        for (Particle particle : particles) {
            particle.draw(g, canvasHeight);
        }
    }

    public void clearParticles() {
        particles.clear(); // Clear the list of particles
    }
    
}
