package gr.uom.api_app;

import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class PostTweetTask extends AsyncTask<Void, Void, Void> {

    String imgPath;
    String text;
    MainActivity mainActivity;
    ArrayList<Post> posts;

    public PostTweetTask(MainActivity mainActivity, String text, String imgPath) {
        this.mainActivity = mainActivity;
        this.text = text;
        this.imgPath = imgPath;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey((MainActivity.getInstance().getString(R.string.CONSUMER_KEY)))
                .setOAuthConsumerSecret(MainActivity.getInstance().getString(R.string.CONSUMER_SECRET))
                .setOAuthAccessToken(MainActivity.getInstance().getString(R.string.ACCESS_TOKEN))
                .setOAuthAccessTokenSecret((MainActivity.getInstance().getString(R.string.ACCESS_TOKEN_SECRET)));

        TwitterFactory twitterFactory = new TwitterFactory(cb.build());
        Twitter twitter = twitterFactory.getInstance();

        StatusUpdate status = new StatusUpdate(text);
        status.setMedia(new File(imgPath));
        try {
            twitter.updateStatus(status);
        } catch (TwitterException e) {
            e.printStackTrace();
        }


        return null;
    }
}
