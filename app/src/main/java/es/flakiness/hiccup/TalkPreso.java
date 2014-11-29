package es.flakiness.hiccup;

import android.net.Uri;

public class TalkPreso {
    private Talk entity;

    public Uri getUri() {
        return Uri.parse(entity.uri);
    }

    public String getTitle() {
        return entity.title;
    }

    public String getDuration() {
        // FIXME: Return appropriate value.
        return "1 minute";
    }

    public TalkPreso(Talk entity) {
        this.entity = entity;
    }

    public long getId() {
        return entity._id.longValue();
    }
}
