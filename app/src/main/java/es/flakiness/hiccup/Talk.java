package es.flakiness.hiccup;

import java.util.Date;

public class Talk {
    public Long _id;
    public String uri;
    public String title;
    public Date duration;

    public Talk() {
    }

    public Talk(Long id, String uri, String title, Date duration) {
        this._id = id;
        this.uri = uri;
        this.title = title;
        this.duration = duration;
    }
}
