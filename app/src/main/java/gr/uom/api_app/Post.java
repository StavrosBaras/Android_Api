package gr.uom.api_app;

import java.io.Serializable;

public class Post implements Serializable {

    private long id;
    private String body;
    private String mediaString;
    private int comments_count;
    private int like_count;
    private String type;
    private String date;
    private String screenName,name;

    public Post(long id, String body, String mediaString, int comments_count, int like_count,String name, String screenName, String date, String type) {
        this.id = id;
        this.body = body;
        this.mediaString = mediaString;
        this.comments_count = comments_count;
        this.date = date;
        this.screenName = screenName;
        this.name = name;
        this.like_count = like_count;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public String getMediaString() {
        return mediaString;
    }

    public int getComments_count() {
        return comments_count;
    }

    public int getLike_count() {
        return like_count;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getName() {
        return name;
    }

}
