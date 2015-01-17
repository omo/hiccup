package es.flakiness.hiccup.play;

import android.widget.TextView;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class PlayClockPreso {

    private final PublishSubject<String> clockTexts = PublishSubject.create();
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public PlayClockPreso(Playing playing) {
        subscriptions.add(playing.progress().subscribe(new Action1<PlayerProgress>() {
            @Override
            public void call(PlayerProgress playerProgress) {
                clockTexts.onNext(toClockText(playerProgress));
            }
        }));
    }

    public void connectTo(final TextView view) {
        // XXX: This should return subscriptions instead of add it to this.
        subscriptions.add(clockTexts.distinctUntilChanged().subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                view.setText(s);
            }
        }));
    }

    public void release() {
        clockTexts.onCompleted();
        subscriptions.unsubscribe();
    }

    private String toClockText(PlayerProgress progress) {
        int second = (progress.getCurrent()/1000)%60;
        int minute = (progress.getCurrent()/(60*1000))%60;
        return String.format("%02d:%02d", minute, second);
    }
}