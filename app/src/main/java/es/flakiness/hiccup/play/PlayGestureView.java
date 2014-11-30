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
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        gestureSubject.onNext(new GestureEvent(GestureEvent.Type.TAP));
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = detector.onTouchEvent(event);
        if (0 != (event.getActionMasked() & MotionEvent.ACTION_UP))
            gestureSubject.onNext(new GestureEvent(GestureEvent.Type.MAY_UNHOLD));
        return ret;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        gestureSubject.onCompleted();
    }

    public Observable<GestureEvent> gestures() {
        return gestureSubject;
    }
}
