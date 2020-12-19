package gr.uom.api_app;

import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PostsActivity extends AppCompatActivity {

    private ArrayList<Post> posts;
    private AccessToken accessToken;
    private String hashtagId;
    private String hashtag;
    private String instaId;
    private ListView postListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        Intent intent = getIntent();

        postListView = findViewById(R.id.postListView);
        posts = new ArrayList<>();
        hashtag = intent.getStringExtra("hashtag");
        accessToken = AccessToken.getCurrentAccessToken();
        hashtagId = intent.getStringExtra("hashtagId");
        instaId = intent.getStringExtra("instaId");

        getInstaPosts();

        postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Stavros", posts.get(position).getMediaString() + " ID : " + posts.get(position).getId() + " text : " + posts.get(position).getBody());
            }
        });

        for(Post post : posts){
            Log.d("Stavros", "-------- " + post.getMediaString() + "  " + post.getBody());
        }

        //searchPostsTask searchPostsTask = new searchPostsTask(hashtag);
        //searchPostsTask.execute();

        /*try {
            posts = searchPostsTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/

    }

    private void getInstaPosts() {
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/" + hashtagId + "/top_media",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response.getJSONObject().getString("data"));

                            for (int i=0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                long id = Long.parseLong(jsonObject.getString("id"));
                                String media_type = jsonObject.getString("media_type");
                                String mediaString = "";
                                if (media_type.equals("IMAGE")) {
                                    mediaString = jsonObject.getString("media_url");
                                }
                                String body = jsonObject.getString("caption");
                                int comments_count = Integer.parseInt(jsonObject.getString("comments_count"));
                                int likes_count = Integer.parseInt(jsonObject.getString("like_count"));

                                posts.add(new Post(id, body, mediaString, comments_count, likes_count, "instagram"));
                            }

                            PostAdapter postAdapter = new PostAdapter(PostsActivity.this,posts);
                            postListView.setAdapter(postAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,media_type,comments_count,like_count,media_url,caption");
        parameters.putString("user_id", instaId);
        request.setParameters(parameters);
        request.executeAsync();
    }
}