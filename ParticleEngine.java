import java.util.List;

public class ParticleEngine implements Runnable {
    private ParticleController particleController;
    private int canvasWidth, canvasHeight;
    private long lastProcessingTime = 0;

    public ParticleEngine(int canvasWidth, int canvasHeight) {
        this.particleController = new ParticleController();
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public ParticleEngine(int canvasWidth, int canvasHeight, List<Particle> particles) {
        this.particleController = new ParticleController(particles);
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        particleController.updateParticles(canvasWidth, canvasHeight); // Assuming no walls to manage
        long endTime = System.currentTimeMillis();
        lastProcessingTime = endTime - startTime;
    }

    public void addParticle(Particle particle) {
        particleController.addParticle(particle);
    }

    public ParticleController getParticleController() {
        return particleController;
    }

    public long getLastProcessingTime() {
        return lastProcessingTime;
    }

    public void clearParticles() {
        particleController.clearParticles(); // Assuming ParticleController has a method to clear particles
    }
}
