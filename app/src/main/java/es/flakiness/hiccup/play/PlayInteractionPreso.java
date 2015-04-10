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

import es.flakiness.hiccup.play.sign.HoldSign;
import es.flakiness.hiccup.play.sign.PauseSign;
import es.flakiness.hiccup.play.sign.PlaySign;
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

    public PlayInteractionPreso(final View view, Observable<GestureEvent> gestures, Observable<PlayerState> states) {
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
                        ((Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(10);
                        break;
                    case UP:
                        animateBackgroundTo(originalColor);
                        break;
                    default:
                        break;
                }
            }
        }));

        subscriptions.add(states.subscribe(new Action1<PlayerState>() {
            @Override
            public void call(PlayerState playerState) {
                switch (playerState) {
                    case PLAYING:
                        invalidations.onNext(new PauseSign());
                        break;
                    case PAUSING:
                        invalidations.onNext(new PlaySign());
                        break;
                    case HOLDING:
                        invalidations.onNext(new HoldSign());
                        break;
                    default:
                        invalidations.onNext(new ViewRenderer() {
                            @Override
                            public void draw(View view, Canvas canvas) {
                                // empty.
                            }
                        });
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
