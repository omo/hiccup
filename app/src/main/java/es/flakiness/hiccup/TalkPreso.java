package es.flakiness.hiccup;

import android.net.Uri;

public class TalkPreso {
    private long id;
    private Uri uri;
    private String title;

    public Uri getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return "1 minute";
    }

    public TalkPreso() {
        uri = Uri.parse("http://foo.com/bar");
        title = "Hello Shadowing Talk!";
    }

    public long getId() {
        return id;
    }
}
