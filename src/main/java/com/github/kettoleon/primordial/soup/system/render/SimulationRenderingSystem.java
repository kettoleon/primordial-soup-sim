package com.github.kettoleon.primordial.soup.system.render;

import com.github.kettoleon.primordial.soup.SimulationSystem;
import com.github.kettoleon.primordial.soup.model.World;

import javax.swing.*;
import java.awt.*;

public class SimulationRenderingSystem implements SimulationSystem {

    private static final int DRAW_EVERY_X_TICKS = 1;
    private SimulationUI simulationUI;

    private long lastTime = System.currentTimeMillis();
    private long lastTick = 0;

    @Override
    public void init(World world) {
        SwingUtilities.invokeLater(() -> {


            JFrame jFrame = new JFrame();
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setTitle("Soup Simulation");

            jFrame.setMinimumSize(new Dimension(800, 600));
            simulationUI = new SimulationUI();
            simulationUI.setWorld(world);
            jFrame.setContentPane(simulationUI);
            jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            jFrame.pack();
            jFrame.setVisible(true);

        });
    }


    @Override
    public void tick(long id, World world) {

        long now = System.currentTimeMillis();
        double elapsedSeconds = (now - lastTime) / 1000;
        if (elapsedSeconds >= 1.0) {

            double elapsedTicks = id - lastTick;
            int ticksPerSecond = (int) Math.floor(elapsedTicks / elapsedSeconds);
            if (simulationUI != null) {
                simulationUI.setTicksPerSecond(ticksPerSecond);
            }
            lastTime = now;
            lastTick = id;
        }

        if (simulationUI != null && id % DRAW_EVERY_X_TICKS == 0) {
            simulationUI.renderBufferedWorld();
            SwingUtilities.invokeLater(() -> {
                simulationUI.repaint();
            });
        }

    }
}
