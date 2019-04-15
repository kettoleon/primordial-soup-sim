package com.github.kettoleon.primordial.soup.system.render;

import com.github.kettoleon.primordial.soup.model.Position;
import com.github.kettoleon.primordial.soup.model.World;
import com.github.kettoleon.primordial.soup.model.WorldObject;
import com.github.kettoleon.primordial.soup.model.creature.Creature;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class SimulationPanel extends JPanel implements MouseMotionListener, MouseListener {

    private long tickId;
    private World world;

    private float offsetX = 0;
    private float offsetY = 0;

    private BufferedImage bufferedImage;
    private Object renderingMutex = new Object();
    private Point dragStart;
    private Point dragEnd;
    public static final int CREATURE_WIDTH = 5;

    public SimulationPanel() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                offsetX = getWidth() / 2;
                offsetY = getHeight() / 2;
                removeComponentListener(this);
            }
        });

        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics graphics) {

        synchronized (renderingMutex) {

            if (bufferedImage != null) {
                graphics.drawImage(bufferedImage, 0, 0, null);
            }

        }

    }


    public void renderBufferedWorld() {
        //TODO to be called from simulation thread
        synchronized (renderingMutex) {
            int width = getWidth();
            int height = getHeight();
            if (width > 0 && height > 0) {
                bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                renderWorld((Graphics2D) bufferedImage.getGraphics(), width, height);
            }
        }
    }


    private void renderWorld(Graphics2D graphics, int width, int height) {
        graphics.setColor(new Color(17, 112, 134));
        graphics.fillRect(0, 0, width, height);

        graphics.setColor(Color.green);
        world.getPlantParticles().stream().filter(p -> !p.isEaten()).forEach(wo -> paintWorldObject(graphics, wo, 2));

//        graphics.setColor(Color.yellow);
//        plants.stream().filter(p -> p.isEaten()).forEach(wo -> paintWorldObject(graphics, wo,1));


        world.getCreatures().stream().filter(creature -> !creature.isDead()).forEach(wo -> paintCreature(graphics, wo));

//        graphics.setColor(Color.black);
//        worldObjectStream.stream().filter(creature -> !creature.isDead()).forEach(wo -> paintText(graphics, wo.getPosition(), wo.hunger()));

        graphics.setColor(Color.black);
        world.getCreatures().stream().filter(creature -> creature.isDead()).forEach(wo -> paintWorldObject(graphics, wo, 2));
    }

    private void paintText(Graphics2D graphics, Position position, float hunger) {
        graphics.drawString(hunger + "", offsetX + position.getX(), offsetY + position.getY());
    }

    private void paintWorldObject(Graphics g, WorldObject wo, int width) {

        g.fillOval((int) (offsetX + wo.getPosition().getX()), (int) (offsetY + wo.getPosition().getY()), width, width);
    }

    private void paintCreature(Graphics2D g, Creature wo) {

        g.setColor(Color.red);
        int screenX = (int) (offsetX + wo.getPosition().getX());
        int screenY = (int) (offsetY + wo.getPosition().getY());

        g.fillOval(screenX - CREATURE_WIDTH / 2, screenY - CREATURE_WIDTH / 2, CREATURE_WIDTH, CREATURE_WIDTH);

        g.setColor(Color.black);
        //getPosition().translate(distance * Math.cos(rotation), distance * Math.sin(rotation));
        int lookingAtX = (int) (screenX + Math.cos(wo.getRotation()) * CREATURE_WIDTH / 2);
        int lookingAtY = (int) (screenY + Math.sin(wo.getRotation()) * CREATURE_WIDTH / 2);
        g.drawLine(screenX, screenY, lookingAtX, lookingAtY);

//        printNoseReach(g, wo);
        printTail(g, wo);

    }

    private void printTail(Graphics2D g, Creature wo) {
        LinkedList<Position> tail = wo.getTrail();
        for (int i = 0; i < tail.size() - 1; i++) {
            Position p1 = tail.get(i);
            Position p2 = tail.get(i + 1);
            g.setColor(new Color(1, 0, 0, 0.8f - i * (0.75f / tail.size())));
            g.drawLine((int) (offsetX + p1.getX()), (int) (offsetY + p1.getY()), (int) (offsetX + p2.getX()), (int) (offsetY + p2.getY()));
        }
    }

    private void printNoseReach(Graphics2D g, Creature wo) {
        g.setColor(new Color(0, 0xff, 0, 0x22));
        int[] xPoints = new int[]{
                (int) (offsetX + wo.getPosition().getX()),
                (int) (offsetX + wo.getSmellTriangle()[1].getX()),
                (int) (offsetX + wo.getSmellTriangle()[2].getX())
        };
        int[] yPoints = new int[]{
                (int) (offsetY + wo.getPosition().getY()),
                (int) (offsetY + wo.getSmellTriangle()[1].getY()),
                (int) (offsetY + wo.getSmellTriangle()[2].getY())
        };
        g.fillPolygon(xPoints, yPoints, 3);

        g.setColor(new Color(0, 0, 0xff, 0x22));
        xPoints = new int[]{
                (int) (offsetX + wo.getPosition().getX()),
                (int) (offsetX + wo.getSmellTriangle()[1].getX()),
                (int) (offsetX + wo.getSmellTriangle()[0].getX())
        };
        yPoints = new int[]{
                (int) (offsetY + wo.getPosition().getY()),
                (int) (offsetY + wo.getSmellTriangle()[1].getY()),
                (int) (offsetY + wo.getSmellTriangle()[0].getY())
        };
        g.fillPolygon(xPoints, yPoints, 3);
    }


    public void setTickId(long tickId) {
        this.tickId = tickId;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void mouseDragged(MouseEvent e) {


    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            dragStart = e.getPoint();
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            dragEnd = e.getPoint();
            setCursor(Cursor.getDefaultCursor());
            offsetX -= dragStart.x - dragEnd.x;
            offsetY -= dragStart.y - dragEnd.y;
            renderBufferedWorld();
            SwingUtilities.invokeLater(() -> {
                repaint();
            });
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
