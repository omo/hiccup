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

    private ViewRenderer lastSign;

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

    private void updateSign(ViewRenderer sign) {
        lastSign = sign;
        invalidations.onNext(sign);
    }

    private void updatePullDelta(float delta) {
        if (!(lastSign instanceof HoldSign))
            return;
        updateSign(new HoldSign(delta));
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
                    case PULL:
                        updatePullDelta(((PullEvent) gestureEvent).getDelta());
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
                        updateSign(new PauseSign());
                        break;
                    case PAUSING:
                        updateSign(new PlaySign());
                        break;
                    case HOLDING:
                        updateSign(new HoldSign(0));
                        break;
                    default:
                        updateSign(new ViewRenderer() {
                            @Override
                            public void draw(ViewRenderingContext context, Canvas canvas) {
                                // empty.
                            }
                        });
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
