package es.flakiness.hiccup.play;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
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
    private ViewRenderer renderer = new ViewRenderer() {
        @Override
        public void draw(ViewRenderingContext context, Canvas canvas) {
            // Do nothing by default.
        }
    };
    private ViewRenderingContext viewRenderingContext;

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
        viewRenderingContext = new ViewRenderingContext(this);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        gestureSubject.onNext(new GestureEvent(GestureEvent.Type.DOWN));
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
        boolean up = 0 != (event.getActionMasked() & MotionEvent.ACTION_UP);
        if (up)
            gestureSubject.onNext(new GestureEvent(GestureEvent.Type.UP));

        if (null != pressedHere) {
            if (up)
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        renderer.draw(viewRenderingContext, canvas);
    }

    private void onRelease(MotionEvent event) {
        gestureSubject.onNext(new GestureEvent(GestureEvent.Type.RELEASE));
        pressedHere = null;
    }

    private void onPull(MotionEvent event) {
        float radius = getWidth()*0.5f;
        float delta = ((float) event.getX() - pressedHere.getX()) / radius;
        gestureSubject.onNext(new PullEvent(delta));
    }

    private void onFlingBack() {
        gestureSubject.onNext(new GestureEvent(GestureEvent.Type.FLING_BACK));
    }

    public Observable<GestureEvent> gestures() {
        return gestureSubject;
    }

    public void willRender(ViewRenderer renderer) {
        this.renderer = renderer;
        invalidate();
    }

}
