import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.Graphics;

public class ThreadManager {
    private List<ParticleEngine> processors = new CopyOnWriteArrayList<>();
    private ExplorerEngine expProcess ;
    private ExecutorService executorService = Executors.newCachedThreadPool(); // Manages threads
    private int canvasWidth, canvasHeight;
    private int particleSize = 0;
    private int explorerSize = 0;


    private int roundRobinIndex = 0;

    private long lastAverageProcessingTime = 0;
    private List<Long> processingTimesHistory = new ArrayList<>();
    private static final int PROCESSING_TIME_HISTORY_SIZE = 20;

    private int lastParticleSizeAtThreadAddition = 0;

    public void setCanvasSize(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        if (processors.isEmpty()) {
            addProcessor();
        }
    }

    public void clearParticles() {
        // Clear all ParticleEngines or reset them
        for (ParticleEngine engine : processors) {
            engine.clearParticles(); // Assume each ParticleEngine has a method to clear its particles
        }
        particleSize = 0; // Reset the count of particles
        // Notify observers or components that the particle count has changed, if necessary
    }
    

    private void addProcessor() {
        ParticleEngine engine = new ParticleEngine(canvasWidth, canvasHeight);
        processors.add(engine);
        executorService.execute(engine);
        lastParticleSizeAtThreadAddition = particleSize;
    }

    private void addProcessor(List<Particle> particles) {
        ParticleEngine engine = new ParticleEngine(canvasWidth, canvasHeight, particles);
        processors.add(engine);
        executorService.execute(engine);
        lastParticleSizeAtThreadAddition = particleSize;
    }


    public void checkAndAdjustThread() {
        if (shouldAddThread()) {
            redistributeParticles();
        }
    }
    public int getExplorerCount(){
        return explorerSize;
    }
    private boolean shouldAddThread() {
        boolean processingTimeIncreasing = false;
        if (!processingTimesHistory.isEmpty() && particleSize >= 1000) {
            long currentAverageProcessingTime = processingTimesHistory.get(processingTimesHistory.size() - 1);
            processingTimeIncreasing = currentAverageProcessingTime > lastAverageProcessingTime;
        }
        boolean significantParticleIncrease = particleSize >= lastParticleSizeAtThreadAddition * 1.10;
        return processingTimeIncreasing && processors.size() < Runtime.getRuntime().availableProcessors() && particleSize != 0 && significantParticleIncrease;
    }

    public void addParticle(Particle particle) {
        if (processors.isEmpty()) {
            addProcessor(); 
        }
        particleSize++;
        ParticleEngine selectedProcessor = processors.get(roundRobinIndex);
        selectedProcessor.addParticle(particle);
        roundRobinIndex = (roundRobinIndex + 1) % processors.size();
        System.out.println("Particle added: " + particle);
        System.out.println("Current particle size: " + particleSize);


     //   try {
      //      Thread.sleep(10);
      //  } catch (InterruptedException ie) {
      //      Thread.currentThread().interrupt();
      //  }
    }

    public void addExplorer(Explorer exp){
        this.expProcess = new ExplorerEngine(canvasWidth,canvasHeight,exp);
        expProcess.addExplorer(exp);
        explorerSize++;
    }
    public void addParticles(List<Particle> particles) {
        if (processors.isEmpty()) {
            addProcessor(); 
        }
        for (Particle particle : particles) {
            addParticle(particle);
        }
        System.out.println("Batch of particles added. Total particles: " + particles.size());
    }

    public void updateParticles() {
        for (ParticleEngine processor : processors) {
            executorService.submit(processor::run);
        }
    }

    public void drawParticles(Graphics g, int canvasHeight) {
        for (ParticleEngine processor : processors) {
            processor.getParticleController().drawParticles(g, canvasHeight);
        }
    }
    public void drawExplorer(Graphics g, int canvasHeight){
        expProcess.getExplorerController().drawExplorer(g, canvasHeight);
    }

    public int getParticleSize() {
        return particleSize;
    }

    private void redistributeParticles() {
        int processorSize = processors.size();
        List<Particle> newParticles = new ArrayList<>();
        for (ParticleEngine processor : processors) {
            List<Particle> extractedParticles = processor.getParticleController().getParticles();
            int popCount = extractedParticles.size() / (processorSize + 1);
            newParticles.addAll(extractedParticles.subList(0, popCount));
            extractedParticles.removeAll(newParticles);
        }
        addProcessor(newParticles);
    }

    public void updateProcessingTimes() {
        long totalProcessingTime = 0;
        for (ParticleEngine processor : processors) {
            totalProcessingTime += processor.getLastProcessingTime();
        }
        long currentAverageProcessingTime = totalProcessingTime / processors.size();
        processingTimesHistory.add(currentAverageProcessingTime);
        if (processingTimesHistory.size() > PROCESSING_TIME_HISTORY_SIZE) {
            processingTimesHistory.remove(0);
        }
        lastAverageProcessingTime = processingTimesHistory.stream().mapToLong(Long::longValue).sum() / processingTimesHistory.size();
    }
}