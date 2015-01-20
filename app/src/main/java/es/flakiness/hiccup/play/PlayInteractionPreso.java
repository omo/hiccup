package es.flakiness.hiccup.play;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class PlayInteractionPreso implements Subscription {
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final int originalColor;

    static private int darken(int color, float level) {
        float hsv[] = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2]* level;
        return Color.HSVToColor(hsv);
    }

    public PlayInteractionPreso(final View view, Observable<GestureEvent> gestures) {
        originalColor = ((ColorDrawable) view.getBackground()).getColor();
        subscriptions.add(gestures.subscribe(new Action1<GestureEvent>() {
            @Override
            public void call(GestureEvent gestureEvent) {
                switch (gestureEvent.getType()) {
                    case DOWN:
                        Log.d(getClass().getSimpleName(), view.getBackground().toString());
                        view.setBackgroundColor(darken(originalColor, 0.9f));
                        break;
                    case HOLD:
                        ((Vibrator)view.getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
                        break;
                    case UP:
                        view.setBackgroundColor(originalColor);
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
