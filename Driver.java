import javax.swing.*;
import java.awt.*;

public class Driver extends JFrame {
    private final SimulationPanel simulationPanel;

    public Driver() {
        simulationPanel = new SimulationPanel();
        initUI();
    }

    private void initUI() {
        setTitle("Particle Physics Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(simulationPanel);
        pack(); 
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Driver driver = new Driver();
            ControlPanel controlPanel = new ControlPanel(driver.simulationPanel.getThreadManager());
            
            // Position ControlPanel to the right of Driver window
            controlPanel.setLocation(driver.getX() + driver.getWidth(), driver.getY());
            controlPanel.setVisible(true);
        });
    }
}
