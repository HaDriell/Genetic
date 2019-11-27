package fr.hadriel;

import fr.hadriel.empires.World;
import fr.hadriel.empires.ai.Characteristics;
import fr.hadriel.empires.ai.Tribe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

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
        World world = new World((int) Instant.now().toEpochMilli(), 4, 1600, 900);

        //TODO : load from a file instead
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            world.spawnTribe(new Characteristics(random));
        }

        new Application(world).mainloop();
    }
}