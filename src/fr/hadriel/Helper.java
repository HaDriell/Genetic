package fr.hadriel;

import fr.hadriel.empires.ai.Characteristics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Helper {
    public static final File ROOT = new File("./genomes");


    public static List<Characteristics> LoadOrGenerateAllGenomes(Random random) {
        List<Characteristics> genomes = new ArrayList<>();

        for (int i = 0; i < 60; i++) {
            // fixed to 60 genomes for now
            File file = new File(ROOT, "genome_" + i + ".properties");

            Characteristics characteristics;
            if (file.exists()) {
                try (FileInputStream stream = new FileInputStream(file)) {
                    characteristics = Characteristics.Load(stream); // Load existing Genome
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                characteristics = new Characteristics(random); // New Random Genome
            }
            genomes.add(characteristics);
        }
        return genomes;
    }

    public static void SaveAllGenomes(List<Characteristics> genomes) {
        if(!ROOT.exists() && !ROOT.mkdir())
            throw new RuntimeException("Failed to create Storage Folder !");

        for (int i = 0; i < genomes.size(); i++) {
            File file = new File(ROOT, "genome_" + i + ".properties");
            try (FileOutputStream stream = new FileOutputStream(file)) {
                genomes.get(i).save(stream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}