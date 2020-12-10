package gr.uom.api_app;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterInstance {

    public static ConfigurationBuilder getConfigurationBuilder() {

        ConfigurationBuilder cb = new ConfigurationBuilder();

        return cb.setDebugEnabled(true)
                .setOAuthConsumerKey("1MVfEPpnoW7F81XTHU404jTLQ")
                .setOAuthConsumerSecret("HDVg5VkwPeeNU57DiNe63dGPxbrToHL6UrTpFQy3nElEWBbVcT")
                .setOAuthAccessToken("1318238160674037763-EErBvGMlyloTz1OVc1WJuvVTXvTaBP")
                .setOAuthAccessTokenSecret("XyUbyNw17Osh3BSVsMbJIQpzpP0oYQ63b9Hqgv4KdOPsh");

    }

    public static Twitter getTwitterInstance() {

        ConfigurationBuilder cb = getConfigurationBuilder();

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        return twitter;

    }
}
