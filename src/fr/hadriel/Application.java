package fr.hadriel;

import fr.hadriel.empires.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.time.Instant;
import java.util.ArrayList;

public class Application extends JFrame {

    private final World world;
    private final Canvas canvas;

    public Application(World world) {
        this.world = world;
        this.canvas = new Canvas();

        //Initialize the UI
        canvas.setSize(world.width, world.height);

        add(canvas);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }


    public void update(float deltaTime) {
        world.update(deltaTime);
    }

    public void render() {
        BufferStrategy swapChain = canvas.getBufferStrategy();
        if (swapChain == null) {
            canvas.createBufferStrategy(3);
            return;
        }
        Graphics2D g = (Graphics2D) swapChain.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        world.render(g);

        g.dispose();
        swapChain.show();
    }

    public void mainloop() {
        Timer timer = new Timer();

        while (!Thread.interrupted()) {
            float dt = timer.elapsed();
            timer.reset();
            update(dt);
            render();

            int frameTimeMS = (int) (timer.elapsed() * 1000);
            if (frameTimeMS < 16) {
                try {
                    Thread.sleep(16 - frameTimeMS);
                } catch (InterruptedException ignore) {}
            }
        }
    }

    public static void main(String... args) {
        World world = new World((int) Instant.now().toEpochMilli(), 2, 800, 450);

        new Application(world).mainloop();
    }
}