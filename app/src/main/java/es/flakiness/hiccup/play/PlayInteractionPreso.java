package es.flakiness.hiccup.play;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
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
                        invalidations.onNext(new PauseSignRenderer());
                        break;
                    case PAUSING:
                        invalidations.onNext(new PlaySignRenderer());
                        break;
                    case HOLDING:
                        invalidations.onNext(new HoldSignRenderer());
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

    private static class Renderers {
        final static float paddingBottom = 100;

        static private RectF getContainerBox(View view) {
            float vw = view.getWidth();
            float vh = view.getHeight();
            float containerWidth = vw;
            float containerHeight = vw;
            float paddingTop = vh - paddingBottom - containerWidth;
            float containerTop = paddingTop;
            float containerLeft = 0;
            return new RectF(containerLeft, containerTop, containerLeft + containerWidth, containerTop + containerHeight);
        }

        static private RectF getCenterBoundingBox(RectF container) {
            float bboxHeight = (container.width()/3.0f)*1.1f;
            float bboxWidth = (container.width()/3.0f)*1.0f;
            float bboxLeft = (container.width() - bboxWidth)/2;
            float bboxTop = container.top + (container.height() - bboxHeight)/2;
            return new RectF(bboxLeft, bboxTop, bboxLeft + bboxWidth, bboxTop + bboxHeight);
        }

        static private RectF getRightBoundingBox(RectF container) {
            RectF bound = getCenterBoundingBox(container);
            float bboxShift = container.width()/10;
            bound.offsetTo(container.right - bound.width() - bboxShift, bound.top);
            return bound;
        }

        static private RectF getLeftBoundingBox(RectF container) {
            RectF bound = getCenterBoundingBox(container);
            float bboxShift = container.width()/10;
            bound.offsetTo(container.left + bboxShift, bound.top);
            return bound;
        }

        static private Paint getSignPaint() {
            Paint paint = new Paint();
            paint.setARGB(255, 0, 0, 0);
            return paint;
        }
    }

    private static class PauseSignRenderer implements ViewRenderer {

        @Override
        public void draw(View view, Canvas canvas) {
            RectF container = Renderers.getContainerBox(view);
            RectF bound = Renderers.getCenterBoundingBox(container);

            float pauseBoxHeight = bound.height();
            float pauseBoxSeparation = bound.width() / 4.0f;
            float pauseBoxWidth = (bound.width() - pauseBoxSeparation) / 2;

            Path path = new Path();
            path.addRect(
                    bound.left,
                    bound.top,
                    bound.left + pauseBoxWidth,
                    bound.top + pauseBoxHeight,
                    Path.Direction.CCW);

            path.addRect(
                    bound.left + pauseBoxWidth + pauseBoxSeparation,
                    bound.top,
                    bound.left + pauseBoxWidth + pauseBoxSeparation + pauseBoxWidth,
                    bound.top + pauseBoxHeight,
                    Path.Direction.CCW);

            canvas.drawPath(path, Renderers.getSignPaint());
        }
    }

    private static class PlaySignRenderer implements ViewRenderer {
        @Override
        public void draw(View view, Canvas canvas) {
            RectF container = Renderers.getContainerBox(view);
            RectF bound = Renderers.getCenterBoundingBox(container);

            Path path = new Path();
            path.moveTo(bound.left, bound.top);
            path.lineTo(bound.right, bound.centerY());
            path.lineTo(bound.left, bound.bottom);
            path.close();

            canvas.drawPath(path, Renderers.getSignPaint());
        }
    }

    private static class HoldSignRenderer implements ViewRenderer {
        @Override
        public void draw(View view, Canvas canvas) {
            RectF container = Renderers.getContainerBox(view);
            RectF rbound = Renderers.getRightBoundingBox(container);

            Path rpath = new Path();
            rpath.moveTo(rbound.left, rbound.top);
            rpath.lineTo(rbound.centerX(), rbound.centerY());
            rpath.lineTo(rbound.left, rbound.bottom);
            rpath.close();
            rpath.moveTo(rbound.centerX(), rbound.top);
            rpath.lineTo(rbound.right, rbound.centerY());
            rpath.lineTo(rbound.centerX(), rbound.bottom);
            rpath.close();

            canvas.drawPath(rpath, Renderers.getSignPaint());

            RectF lbound = Renderers.getLeftBoundingBox(container);

            Path lpath = new Path();
            lpath.moveTo(lbound.right, lbound.top);
            lpath.lineTo(lbound.centerX(), lbound.centerY());
            lpath.lineTo(lbound.right, lbound.bottom);
            lpath.close();
            lpath.moveTo(lbound.centerX(), lbound.top);
            lpath.lineTo(lbound.left, lbound.centerY());
            lpath.lineTo(lbound.centerX(), lbound.bottom);
            lpath.close();

            canvas.drawPath(lpath, Renderers.getSignPaint());
        }
    }
}
