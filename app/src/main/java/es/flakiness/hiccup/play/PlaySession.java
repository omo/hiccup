package es.flakiness.hiccup.play;

import android.net.Uri;

import javax.inject.Inject;

import es.flakiness.hiccup.talk.TalkStore;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class PlaySession implements Subscription {
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final Uri uri;
    private final TalkStore talkStore;
    private PlayerProgress lastProgress;

    @Inject public PlaySession(Uri uri, Playing playing, TalkStore talkStore) {
        this.uri = uri;
        this.talkStore = talkStore;
        this.subscriptions.add(playing.progress().subscribe(new Action1<PlayerProgress>() {
            @Override
            public void call(PlayerProgress playerProgress) {
                lastProgress = playerProgress;
            }
        }));
    }

    @Override
    public void unsubscribe() {
        subscriptions.unsubscribe();
        talkStore.updateLastPosition(uri, lastProgress.getCurrent());
    }

    @Override
    public boolean isUnsubscribed() {
        return subscriptions.isUnsubscribed();
    }
}
