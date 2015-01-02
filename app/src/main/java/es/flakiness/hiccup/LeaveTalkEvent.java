package es.flakiness.hiccup;

import android.net.Uri;

public class LeaveTalkEvent {
    private Uri uri;
    private int leavingPosition;

    public int getLeavingPosition() {
        return leavingPosition;
    }

    public Uri getUri() {
        return uri;
    }

    public LeaveTalkEvent(Uri uri, int leavingPosition) {
        this.uri = uri;
        this.leavingPosition = leavingPosition;
    }
}
