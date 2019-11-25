package fr.hadriel;

public class Timer {

    private long start = System.nanoTime();

    public float elapsed() {
        long now = System.nanoTime();
        return (now - start) / 1e9f;
    }

    public void reset() {
        start = System.nanoTime();
    }
}
