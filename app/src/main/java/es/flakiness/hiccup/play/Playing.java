package es.flakiness.hiccup.play;

import android.net.Uri;

import rx.Observable;

public interface Playing {
    Uri getUri();

    Observable<PlayerProgress> progress();
    Observable<PlayerState> states();
}
