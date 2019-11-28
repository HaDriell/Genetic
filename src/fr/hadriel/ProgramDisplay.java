package fr.hadriel;

import fr.hadriel.empires.World;
import fr.hadriel.empires.ai.Characteristics;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProgramDisplay extends JFrame {

    private final World world;
    private final Canvas canvas;

    public ProgramDisplay(World world) {
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

            if (timer.elapsed() > 0.016f) {
                timer.reset();
                update(0.1f);
                render();
            }
            if(!Thread.interrupted()) continue;

            float dt = timer.elapsed();
            timer.reset();
            update(0.1f);
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
        Random random = new Random();
        List<Characteristics> genomes = Helper.LoadOrGenerateAllGenomes(random);
        World world = new World(random.nextInt(), 4, 800, 450);

        for (int i = 0; i < 10; i++) {
            Characteristics characteristics = genomes.get(random.nextInt(genomes.size()));
            world.spawnTribe(characteristics, true);
        }

        new ProgramDisplay(world).mainloop();
    }
}