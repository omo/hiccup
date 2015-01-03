package es.flakiness.hiccup.play;

import android.os.Handler;

import rx.Observable;
import rx.subjects.PublishSubject;

public class PlayerProgressSource {

    public static interface Values {
        int getCurrentPosition();
        PlayerProgress getProgress();
    }

    private static int UPDATE_INTERVAL = 1000;

    final private PublishSubject<PlayerProgress> subject = PublishSubject.create();
    final private Values values;
    private Handler handler;

    final private Runnable postProgress = new Runnable() {
        @Override
        public void run() {
            emit();
            if (handler != null) {
                int delay = UPDATE_INTERVAL - values.getCurrentPosition() % UPDATE_INTERVAL;
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

    public void emit() {
        emit(values.getProgress());
    }

    public void emit(PlayerProgress progress) {
        if (subject.hasObservers() && null != progress)
            subject.onNext(progress);
    }

    public Observable<PlayerProgress> getObservable() {
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (handler != null)
                        emit();
                }
            });
        }

        return subject;
    }
}
