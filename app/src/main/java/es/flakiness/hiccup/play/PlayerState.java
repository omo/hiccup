package es.flakiness.hiccup.play;

// FIXME:
//   This seems redundant. We should be able to infer some of these from MediaPlayer API.
//   Probably we could extract the non-policy part of the GestureInterpreter into a class and
//   give getState() api to that, instead of explicitly storing the state.
public enum PlayerState {
    PREPARING,
    PREPARED,
    PLAYING,
    PAUSING,
    HOLDING,
    SEEKING;

    public boolean isReadyToStart() {
        return !isBusy() && this != PLAYING;
    }

    public boolean isBusy() {
        return this == PREPARING || this == SEEKING;
    }

    public boolean isPauseable() {
        return this == PLAYING;
    }

    public boolean isHoldable() {
        return this == PLAYING || this == PAUSING;
    }
}
