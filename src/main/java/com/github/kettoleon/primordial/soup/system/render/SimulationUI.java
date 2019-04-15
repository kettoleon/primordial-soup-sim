package com.github.kettoleon.primordial.soup.system.render;

import com.github.kettoleon.primordial.soup.model.World;

import javax.swing.*;
import java.awt.*;

public class SimulationUI extends JPanel {

    private final SimulationPanel simPanel;
    private final JLabel tickLabel;
    private final JLabel tpsLabel;

    public SimulationUI() {
        setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar("Toolbar");
        toolBar.setFloatable(false);

        add(toolBar, BorderLayout.PAGE_START);
        toolBar.add(new JButton("Play"));
        toolBar.add(new JButton("Pause"));
        toolBar.add(new JButton("Set speed..."));
        tickLabel = new JLabel("Tick: ");
        tpsLabel = new JLabel("TPS: ");
        toolBar.add(tickLabel);
        toolBar.add(tpsLabel);

        simPanel = new SimulationPanel();
        add(simPanel, BorderLayout.CENTER);

    }

    public void setTicksPerSecond(int ticksPerSecond) {
        tpsLabel.setText("TPS: " + ticksPerSecond);
    }

    public void setWorld(World world) {
        simPanel.setWorld(world);
    }

    public void renderBufferedWorld() {
        simPanel.renderBufferedWorld();
    }
}
