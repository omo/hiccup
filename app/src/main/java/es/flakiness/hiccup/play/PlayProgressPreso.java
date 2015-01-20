package es.flakiness.hiccup.play;

import android.widget.TextView;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class PlayProgressPreso implements Subscription {

    private final Playing playing;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public PlayProgressPreso(Playing playing) {
        this.playing = playing;
    }

    public void connectTo(final TextView textView, final PlayBarView barView) {
        subscriptions.add(this.playing.progress().map(new Func1<PlayerProgress, String>() {
            @Override
            public String call(PlayerProgress playerProgress) {
                return toClockText(playerProgress);
            }
        }).distinctUntilChanged().subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                textView.setText(s);
            }
        }));

        subscriptions.add(this.playing.progress().subscribe(new Action1<PlayerProgress>() {
            @Override
            public void call(PlayerProgress playerProgress) {
                float c = playerProgress.getCurrent();
                float d = playerProgress.getDuration();
                barView.setProgress(c/d);
            }
        }));
    }

    private String toClockText(PlayerProgress progress) {
        int second = (progress.getCurrent()/1000)%60;
        int minute = (progress.getCurrent()/(60*1000))%60;
        return String.format("%02d:%02d", minute, second);
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
