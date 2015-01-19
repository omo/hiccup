package es.flakiness.hiccup.play;

import android.os.Handler;

import rx.Observable;
import rx.subjects.PublishSubject;

public class PlayerProgressSource {

    public static interface Values {
        PlayerProgress getProgress();
        PlayerState getState();
    }

    private static int UPDATE_INTERVAL = 1000;

    final private PublishSubject<PlayerProgress> subject = PublishSubject.create();
    final private Values values;
    private Handler handler;

    final private Runnable postProgress = new Runnable() {
        @Override
        public void run() {
            emitIfStateAllows();
            if (handler != null) {
                int delay = UPDATE_INTERVAL - values.getProgress().getCurrent() % UPDATE_INTERVAL;
                handler.postDelayed(postProgress, delay);
            }
        }
    };

    public PlayerProgressSource(Values values, Handler handler) {
        this.values = values;
        this.handler = handler;
    }

    public void start() {
        this.handler.postDelayed(postProgress, 0);
    }

    public void stop() {
        handler.removeCallbacks(postProgress);
        handler = null;
    }

    public void emit(PlayerProgress progress) {
        if (subject.hasObservers() && null != progress)
            subject.onNext(progress);
    }

    public Observable<PlayerProgress> getObservable() {
        if (shouldEmit()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    emitIfStateAllows();
                }
            });
        }

        return subject;
    }

    // FIXME:
    //   This smells wrong. We should have two observable
    //   for both seeker and this one, then caller switches which to use.
    private void emitIfStateAllows() {
        if (shouldEmit())
            emit(values.getProgress());
    }

    private boolean shouldEmit() {
        return handler != null && values.getState().shouldEmit();
    }
}
