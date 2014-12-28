package es.flakiness.hiccup.play;

public enum PlayerState {
    PREPARING,
    PREPARED,
    PLAYING,
    PAUSING,
    HOLDING,
    COMPLETED,
    SEEKING,
    SEEKED;

    public boolean isReadyToStart() {
        return this == PREPARED || this == PAUSING || this == HOLDING || this == SEEKED;
    }

    public boolean isPauseable() {
        return this == PLAYING;
    }

    public boolean isHoldable() {
        return this == PLAYING || this == PAUSING;
    }
}
