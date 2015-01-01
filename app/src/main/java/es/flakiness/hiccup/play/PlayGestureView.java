package es.flakiness.hiccup.play;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import rx.Observable;
import rx.subjects.PublishSubject;

public class PlayGestureView extends View implements GestureDetector.OnGestureListener {

    private MotionEvent pressedHere;

    private GestureDetector detector;
    private PublishSubject<GestureEvent> gestureSubject = PublishSubject.create();

    public PlayGestureView(Context context) {
        this(context, null);
    }

    public PlayGestureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayGestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PlayGestureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        detector = new GestureDetector(getContext(), this);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        gestureSubject.onNext(new GestureEvent(GestureEvent.Type.HOLD));
        pressedHere = motionEvent;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        gestureSubject.onNext(new GestureEvent(GestureEvent.Type.TAP));
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float dx, float dy) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float vx, float vy) {
        boolean flingToLeft = vx < 0.0 && Math.abs(vx) > Math.abs(vy);
        if (flingToLeft)
            onFlingBack();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = detector.onTouchEvent(event);
        if (null != pressedHere) {
            if (0 != (event.getActionMasked() & MotionEvent.ACTION_UP))
                onRelease(event);
            else
                onPull(event);
        }

        return ret;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        gestureSubject.onCompleted();
    }

    private void onRelease(MotionEvent event) {
        gestureSubject.onNext(new GestureEvent(GestureEvent.Type.RELEASE));
        pressedHere = null;
    }

    private void onPull(MotionEvent event) {
        float delta = ((float) event.getX() - pressedHere.getX()) / getWidth();
        gestureSubject.onNext(new PullEvent(delta));
    }

    private void onFlingBack() {
        gestureSubject.onNext(new GestureEvent(GestureEvent.Type.FLING_BACK));
    }

    public Observable<GestureEvent> gestures() {
        return gestureSubject;
    }
}
