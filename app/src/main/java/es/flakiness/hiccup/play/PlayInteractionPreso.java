package es.flakiness.hiccup.play;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private final PublishSubject<ViewRenderer> invalidation = PublishSubject.create();
    private final View backgroundView;
    private final int originalColor;
    private ValueAnimator backgroundAnimator;

    private ViewRenderer foregroundRenderer = new ViewRenderer() {
        @Override
        public void draw(View view, Canvas canvas) {

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

    public Observable<ViewRenderer> invalidation() {
        return invalidation;
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
