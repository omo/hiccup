package es.flakiness.hiccup.play;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class PlayInteractionPreso implements Subscription {
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final PublishSubject<ViewRenderer> invalidations = PublishSubject.create();
    private final View backgroundView;
    private final int originalColor;
    private ValueAnimator backgroundAnimator;

    private ViewRenderer foregroundRenderer = new ViewRenderer() {
        @Override
        public void draw(View view, Canvas canvas) {
            float vw = view.getWidth();
            float vh = view.getHeight();

            float paddingBottom = 100;
            float margin  = 10;
            // We assume portrait layout, thus shorter one is vw.
            float containerUnit = vw;
            float paddingTop = vh - paddingBottom - containerUnit;
            float containerTop = paddingTop;
            float containerLeft = 0;

            float bboxHeight = (containerUnit/3.0f)*1.1f;
            float bboxWidth = (containerUnit/3.0f)*1.0f;
            float bboxLeft = (containerUnit - bboxWidth)/2;
            float bboxTop = containerTop + (containerUnit - bboxHeight)/2;

            float pauseBoxHeight = bboxHeight;
            float pauseBoxSeparation = bboxWidth/4.0f;
            float pauseBoxWidth = (bboxWidth - pauseBoxSeparation)/2;

            float innerUnit = containerUnit - margin*2;

            Path path = new Path();
            path.addRect(
                    bboxLeft,
                    bboxTop,
                    bboxLeft + pauseBoxWidth,
                    bboxTop + pauseBoxHeight,
                    Path.Direction.CCW);

            path.addRect(
                    bboxLeft + pauseBoxWidth + pauseBoxSeparation,
                    bboxTop,
                    bboxLeft + pauseBoxWidth + pauseBoxSeparation + pauseBoxWidth,
                    bboxTop + pauseBoxHeight,
                    Path.Direction.CCW);

            Paint paint = new Paint();
            paint.setARGB(255, 0, 0, 0);
            canvas.drawPath(path, paint);
        }
    };

    static private int darken(int color, float level) {
        float hsv[] = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2]* level;
        return Color.HSVToColor(hsv);
    }

    private int getBackgroundColor() {
        return ((ColorDrawable) backgroundView.getBackground()).getColor();
    }

    public Observable<ViewRenderer> invalidations() {
        return invalidations;
    }

    private void animateBackgroundTo(int toColor) {
        if (backgroundAnimator != null)
            backgroundAnimator.cancel();
        backgroundAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), getBackgroundColor(), toColor).setDuration(80);
        backgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                backgroundView.setBackgroundColor((Integer)animation.getAnimatedValue());
            }
        });
        backgroundAnimator.start();
    }

    public void startRendering() {
        invalidations.onNext(foregroundRenderer);
    }

    public PlayInteractionPreso(final View view, Observable<GestureEvent> gestures) {
        backgroundView = view;
        originalColor = getBackgroundColor();
        subscriptions.add(gestures.subscribe(new Action1<GestureEvent>() {
            @Override
            public void call(GestureEvent gestureEvent) {
                switch (gestureEvent.getType()) {
                    case DOWN:
                        Log.d(getClass().getSimpleName(), view.getBackground().toString());
                        animateBackgroundTo(darken(originalColor, 0.9f));
                        break;
                    case HOLD:
                        ((Vibrator)view.getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(10);
                        break;
                    case UP:
                        animateBackgroundTo(originalColor);
                        break;
                    default:
                        break;
                }
            }
        }));
    }

    @Override
    public void unsubscribe() {
        subscriptions.unsubscribe();
    }

    @Override
    public boolean isUnsubscribed() {
        return subscriptions.isUnsubscribed();
    }
}
