package es.flakiness.hiccup.talk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AddTalkEvent {
    private Context context;
    private Intent intent;

    public AddTalkEvent(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }


    public Context getContext() {
        return context;
    }

    public Intent getIntent() {
        return intent;
    }

    public Uri getUri() {
        return intent.getData();
    }
}
