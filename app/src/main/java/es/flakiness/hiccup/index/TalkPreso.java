package es.flakiness.hiccup.index;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;

import es.flakiness.hiccup.talk.Talk;

public class TalkPreso {
    private Talk entity;

    public Uri getUri() {
        return Uri.parse(entity.uri);
    }

    public String getTitle() {
        return entity.title;
    }

    public int getLastPosition() {
        if (null == entity.lastPosition)
            return 0;
        return entity.lastPosition.intValue();
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
