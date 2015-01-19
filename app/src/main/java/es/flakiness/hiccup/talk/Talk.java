package es.flakiness.hiccup.talk;

public class Talk {
    public Long _id;
    public String uri;
    public String title;
    public Long duration;
    public Long lastPosition;

    public Talk() {
    }

    public Talk(Long id, String uri, String title, Long duration) {
        this._id = id;
        this.uri = uri;
        this.title = title;
        this.duration = duration;
        this.lastPosition = new Long(0);
    }
}
