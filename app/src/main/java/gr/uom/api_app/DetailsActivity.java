package gr.uom.api_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DetailsActivity extends AppCompatActivity {

    private Post post;
    private String name,screeName,date,mediaURL,type,body;
    private int like_count,comment_count;
    private ImageView mediaView;
    private TextView likeView,commentView,dateView,nameView,screenNameView,bodyView;
    private ListView commentsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();

        post = (Post)intent.getSerializableExtra("post");
        name = post.getName();
        screeName = post.getScreenName();
        if(!screeName.isEmpty())
            screeName = "@" + screeName;
        date = post.getDate();
        mediaURL = post.getMediaString();
        body = post.getBody();
        like_count = post.getLike_count();
        comment_count = post.getComments_count();
        type = post.getType();

        if(type.equals("twitter") && !mediaURL.isEmpty() )
            mediaURL = addChar(mediaURL,'s',4);

        commentsListView = findViewById(R.id.commentsListView);
        mediaView = findViewById(R.id.mediaImageView);
        dateView = findViewById(R.id.dateTXT);
        nameView = findViewById(R.id.nameTXT);
        screenNameView = findViewById(R.id.screenNameTXT);
        likeView = findViewById(R.id.likeTXT);
        commentView = findViewById(R.id.commentTXT);
        bodyView = findViewById(R.id.bodyTXT);

        if(!mediaURL.isEmpty())
            Picasso.get().load(mediaURL).into(mediaView);

        dateView.setText(date);
        nameView.setText(name);
        screenNameView.setText(screeName);
        bodyView.setText(body);
        likeView.setText("Like Count : " + like_count);
        commentView.setText("Comment Count : " + comment_count);

        if(type.equals("twitter")) {
            List<String> comments = new ArrayList<>();
            GetCommentsTask getCommentsTask = new GetCommentsTask(post);
            getCommentsTask.execute();
            try {
                comments = getCommentsTask.get();
                ArrayAdapter<String> commentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, comments);
                commentsListView.setAdapter(commentAdapter);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public String addChar(String str, char ch, int position) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(position, ch);
        return sb.toString();
    }
}