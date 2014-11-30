package es.flakiness.hiccup.play;

public class GestureEvent {
    public enum Type {
        TAP,
        HOLD,
        MAY_UNHOLD
    }

    private final Type type;

    public Type getType() {
        return type;
    }

    public GestureEvent(Type type) {
        this.type = type;
    }
}
