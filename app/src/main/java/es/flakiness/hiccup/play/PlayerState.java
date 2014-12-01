package es.flakiness.hiccup.play;

public enum PlayerState {
    PREPARING,
    PREPARED,
    PLAYING,
    PAUSING,
    HOLDING,
    COMPLETED;

    public boolean isReadyToStart() {
        return this == PREPARED || this == PAUSING || this == HOLDING;
    }

    public boolean isPauseable() {
        return this == PLAYING;
    }
}
