package es.flakiness.hiccup.play;

public class GestureEvent {
    public enum Type {
        TAP,
        HOLD,
        RELEASE,
        PULL
    }

    private final Type type;

    public Type getType() {
        return type;
    }

    public GestureEvent(Type type) {
        this.type = type;
    }
}
