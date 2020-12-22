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

public class SearchTwitterPostsTask extends AsyncTask<Void, Void, ArrayList<Post>> {

    String hashtag;
    MainActivity mainActivity;
    ArrayList<Post> posts;

    public SearchTwitterPostsTask(MainActivity mainActivity, String hashtag) {
        this.mainActivity = mainActivity;
        this.hashtag = hashtag;
    }

    @Override
    protected ArrayList<Post> doInBackground(Void... voids) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey((MainActivity.getInstance().getString(R.string.CONSUMER_KEY)))
                .setOAuthConsumerSecret(MainActivity.getInstance().getString(R.string.CONSUMER_SECRET))
                .setOAuthAccessToken(MainActivity.getInstance().getString(R.string.ACCESS_TOKEN))
                .setOAuthAccessTokenSecret((MainActivity.getInstance().getString(R.string.ACCESS_TOKEN_SECRET)));

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
            int like_count,comment_count;

            for(twitter4j.Status status : result.getTweets()){
                id = status.getId();
                body = status.getText();
                mediaURL= "";
                like_count = status.getFavoriteCount();
                comment_count = status.getRetweetCount();
                for (MediaEntity mediaEntity : status.getMediaEntities()) {
                   //if (mediaEntity.getType().equals("photo"))
                       mediaURL = mediaEntity.getMediaURL();
                   break;
                }
                Log.d("stavros","Post Text: " + status.getText() + "\n\n" );
                Log.d("stavros","Media url: " + mediaURL );
                posts.add(new Post(id,body,mediaURL,comment_count,like_count,"twitter"));
            }

        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return posts;
    }
}
