package es.flakiness.hiccup.talk;

import java.util.Date;

public class Talk {
    public Long _id;
    public String uri;
    public String title;
    public Long duration;

    public Talk() {
    }

    public Talk(Long id, String uri, String title, Long duration) {
        this._id = id;
        this.uri = uri;
        this.title = title;
        this.duration = duration;
    }
}
