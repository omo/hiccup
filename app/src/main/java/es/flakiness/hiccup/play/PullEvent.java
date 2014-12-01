package es.flakiness.hiccup.play;

public class PullEvent extends GestureEvent {

    private final float delta;

    public PullEvent(float delta) {
        super(Type.PULL);
        this.delta = delta;
    }

    public float getDelta() {
        return delta;
    }
}
