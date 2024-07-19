import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
    private final List<ParticleEngine> processors = new CopyOnWriteArrayList<>();
    private ExplorerEngine explorerEngine;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private int canvasWidth, canvasHeight;
    private int particleCount = 0;
    private boolean isExplorerMode = false;  // Toggle between modes
    private int roundRobinIndex = 0;
    private long lastAverageProcessingTime = 0;
    private final List<Long> processingTimesHistory = new ArrayList<>();
    private static final int PROCESSING_TIME_HISTORY_SIZE = 20;
    private int lastParticleCountAtThreadAddition = 0;

    public void setCanvasSize(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        if (processors.isEmpty()) {
            addProcessor();
        }
    }

    public boolean isExplorerMode() {
        return isExplorerMode;
    }

    public boolean toggleMode() {
        isExplorerMode = !isExplorerMode;
        if (isExplorerMode && explorerEngine == null) {
            explorerEngine = new ExplorerEngine(canvasWidth, canvasHeight);
            executorService.execute(explorerEngine);
        } else if (explorerEngine != null) {
            explorerEngine.pause();  // Assume pause() method stops the explorer engine appropriately.
        }
        return isExplorerMode;
    }

    public void clearParticles() {
        for (ParticleEngine engine : processors) {
            engine.clearParticles();
        }
        particleCount = 0;
    }

    private void addProcessor() {
        ParticleEngine engine = new ParticleEngine(canvasWidth, canvasHeight);
        processors.add(engine);
        executorService.execute(engine);
        lastParticleCountAtThreadAddition = particleCount;
    }

    public void addProcessor(List<Particle> particles) {
        ParticleEngine engine = new ParticleEngine(canvasWidth, canvasHeight, particles);
        processors.add(engine);
        executorService.execute(engine);
        lastParticleCountAtThreadAddition = particleCount;
    }

    public synchronized void addParticle(Particle particle) {
        if (processors.isEmpty()) {
            addProcessor();
        }
        particleCount++;
        ParticleEngine selectedProcessor = processors.get(roundRobinIndex);
        selectedProcessor.addParticle(particle);
        roundRobinIndex = (roundRobinIndex + 1) % processors.size();
    }

    public void addParticles(List<Particle> particles) {
        if (processors.isEmpty()) {
            addProcessor(particles);  // Make sure the method call aligns with the available method signatures.
        } else {
            particles.forEach(this::addParticle);
        }
    }

    public void updateParticles() {
        if (!isExplorerMode) {
            for (ParticleEngine processor : processors) {
                executorService.submit(processor::run);
            }
        }
    }

    public void drawParticles(Graphics g, int canvasHeight) {
        for (ParticleEngine processor : processors) {
            processor.getParticleController().drawParticles(g, canvasHeight);
        }
    }

    public void drawExplorer(Graphics g, int canvasHeight) {
        if (explorerEngine != null) {
            explorerEngine.getExplorerController().drawExplorer(g, canvasHeight);
        }
    }

    public int getParticleCount() {
        return particleCount;
    }

    public void checkAndAdjustThread() {
        if (shouldAddThread()) {
            redistributeParticles();
        }
    }

    private boolean shouldAddThread() {
        boolean processingTimeIncreasing = !processingTimesHistory.isEmpty() &&
            processingTimesHistory.get(processingTimesHistory.size() - 1) > lastAverageProcessingTime;
        boolean significantParticleIncrease = particleCount >= lastParticleCountAtThreadAddition * 1.10;
        return processingTimeIncreasing && processors.size() < Runtime.getRuntime().availableProcessors() && significantParticleIncrease;
    }

    private void redistributeParticles() {
        int processorCount = processors.size();
        List<Particle> newParticles = new ArrayList<>();
        for (ParticleEngine processor : processors) {
            List<Particle> extractedParticles = processor.getParticleController().getParticles();
            int popCount = extractedParticles.size() / (processorCount + 1);
            newParticles.addAll(extractedParticles.subList(0, popCount));
            extractedParticles.removeAll(newParticles);
        }
        addProcessor(newParticles);
    }

    public void updateProcessingTimes() {
        long totalProcessingTime = processors.stream().mapToLong(ParticleEngine::getLastProcessingTime).sum();
        long currentAverageProcessingTime = totalProcessingTime / processors.size();
        processingTimesHistory.add(currentAverageProcessingTime);
        if (processingTimesHistory.size() > PROCESSING_TIME_HISTORY_SIZE) {
            processingTimesHistory.remove(0);
        }
        lastAverageProcessingTime = processingTimesHistory.stream().mapToLong(Long::longValue).sum() / processingTimesHistory.size();
    }
}
