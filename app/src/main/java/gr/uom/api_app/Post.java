package gr.uom.api_app;

public class Post {

    private long id;
    private String body;
    private String mediaString;
    private int comments_count;
    private int like_count;
    private String type;

    public Post(long id, String body, String mediaString, int comments_count, int like_count, String type) {
        this.id = id;
        this.body = body;
        this.mediaString = mediaString;
        this.comments_count = comments_count;
        this.like_count = like_count;
        this.type = type;
    }

    public Post(long id, String body, String mediaURL) {
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

    public void setBody(String body) {
        this.body = body;
    }

    public String getMediaString() {
        return mediaString;
    }

    public void setMediaString(String mediaString) {
        this.mediaString = mediaString;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
