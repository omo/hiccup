package es.flakiness.hiccup.play;

public class PlayerProgress {
    private final int duration;
    private final int current;

    public PlayerProgress(int duration, int current) {
        this.duration = duration;
        this.current = current;
    }

    public int getCurrent() {
        return current;
    }

    public int getDuration() {
        return duration;
    }
}
