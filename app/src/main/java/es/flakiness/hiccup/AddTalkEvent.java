package es.flakiness.hiccup;

import android.content.Context;
import android.net.Uri;

public class AddTalkEvent {
    private Context context;
    private Uri uri;

    public AddTalkEvent(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public Context getContext() {
        return context;
    }
}
