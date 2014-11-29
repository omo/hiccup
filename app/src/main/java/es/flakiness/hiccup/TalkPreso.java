package es.flakiness.hiccup;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TalkPreso {
    private Talk entity;

    public Uri getUri() {
        return Uri.parse(entity.uri);
    }

    public String getTitle() {
        return entity.title;
    }

    public String getDuration() {
        return new SimpleDateFormat("mm:ss").format(new Date(entity.duration.longValue()));
    }

    public TalkPreso(Talk entity) {
        this.entity = entity;
    }

    public long getId() {
        return entity._id.longValue();
    }
}
