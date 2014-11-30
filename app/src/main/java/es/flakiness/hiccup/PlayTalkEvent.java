package es.flakiness.hiccup;

import android.net.Uri;

public class PlayTalkEvent {
    private Uri uri;

    public PlayTalkEvent(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }
}
