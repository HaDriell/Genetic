package fr.hadriel;

import fr.hadriel.empires.World;
import fr.hadriel.empires.ai.Characteristics;
import fr.hadriel.empires.ai.Tribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ProgramGeneticTraining {

    private static class LifeCycle implements Runnable {
        public final World world;
        public final float duration;

        public LifeCycle(List<Characteristics> characteristics, int seed, int scale, int width, int height, float duration) {
            this.duration = duration;
            this.world = new World(seed, scale, width, height);
            for(int i = 0; i < characteristics.size(); i++) {
                world.spawnTribe(characteristics.get(i), true);
            }
        }

        public void run() {
            Timer timer = new Timer();
            while (timer.elapsed() < duration) {
                world.update(0.1f);
            }
        }
    }

    public static void main(String... args) {
        Random random = new Random();

        System.out.println("Loading the Generations");
        List<Characteristics> genomes = Helper.LoadOrGenerateAllGenomes(random);

        int tribeCountPerWorld = 10;
        int g = 0;
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        while (!Thread.interrupted()) {
            System.out.println("Starting Generation " + ++g);
            //Randomize genomes to avoid local neighbor genes issues
            Collections.shuffle(genomes);
            //Select a single Seed for one Generation
            int seed = random.nextInt();

            System.out.println("Executing Generation " + g + " LifeCycles");
            //Generate Cycles
            List<LifeCycle> cycles = new ArrayList<>();
            List<Future<?>> futures = new ArrayList<>();
            for (int offset = 0; offset < genomes.size(); offset += tribeCountPerWorld) {
                LifeCycle cycle = new LifeCycle(genomes.subList(offset, offset + tribeCountPerWorld), seed, 4, 800, 450, 5.0f);
                Future<?> future = executor.submit(cycle);
                cycles.add(cycle);
                futures.add(future);
            }

            //Wait for all LifeCycles to finish
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.println("Evaluating Genomes of Generation " + g );
            //Evaluate Characteristics
            List<Characteristics> ranked = cycles.stream()
                    .flatMap(lc -> lc.world.tribes.stream())
                    .sorted(Comparator.comparing(tribe -> tribe.villages.size() + tribe.units.size()))
                    .map(tribe -> tribe.characteristics)
                    .collect(Collectors.toList());
            Collections.reverse(ranked);//best are first now

            //Process the generation's end result
            List<Characteristics> updatedGenomes = new ArrayList<>();
            //First half is
            for (int i = 0; i < genomes.size() / 2; i++) {
                updatedGenomes.add(ranked.get(i));
            }

            for (int i = 0; i < genomes.size() / 2; i++) {
                Characteristics a = ranked.get(random.nextInt(ranked.size() / 2));
                Characteristics b = ranked.get(random.nextInt(ranked.size() / 2));
                updatedGenomes.add(Characteristics.CreateChild(random, a, b));
            }
            genomes = updatedGenomes;

            System.out.println("Saving Generation " + g);
            Helper.SaveAllGenomes(genomes);
        }
        executor.shutdown();
    }
}