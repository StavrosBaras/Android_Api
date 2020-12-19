package gr.uom.api_app;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class searchPostsTask extends AsyncTask<Void, Void, ArrayList<Post>> {

    String hashtag;
    ArrayList<Post> posts;

    public searchPostsTask(String hashtag) {
        this.hashtag = hashtag;
    }

    @Override
    protected ArrayList<Post> doInBackground(Void... voids) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("1MVfEPpnoW7F81XTHU404jTLQ")
                .setOAuthConsumerSecret("HDVg5VkwPeeNU57DiNe63dGPxbrToHL6UrTpFQy3nElEWBbVcT")
                .setOAuthAccessToken("1318238160674037763-rG0cPs2zsBvBGXv3zz5yySEzq8STyD")
                .setOAuthAccessTokenSecret("cC0ULX7naVP0eplmQqBWzvUJG1rOdYsN5M4MI0fkOr0Wo");

        TwitterFactory twitterFactory = new TwitterFactory(cb.build());
        Twitter twitter = twitterFactory.getInstance();

        posts = new ArrayList<>();

        Query query = new Query("#" + hashtag);
        QueryResult result;
        try {
            result = twitter.search(query);
            long id;
            String body="";
            String mediaURL="";

            for(twitter4j.Status status : result.getTweets()){
                id = status.getId();
                body = status.getText();
                mediaURL= "";
                for (MediaEntity mediaEntity : status.getMediaEntities()) {
                   //if (mediaEntity.getType().equals("photo"))
                       mediaURL = mediaEntity.getMediaURL();
                   break;
                }
                Log.d("stavros","Post Text: " + status.getText() + "\n\n" );
                Log.d("stavros","Media url: " + mediaURL );
                posts.add(new Post(id,body,mediaURL));
            }

        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return posts;
    }
}
