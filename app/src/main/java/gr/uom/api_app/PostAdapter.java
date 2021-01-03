package gr.uom.api_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends ArrayAdapter {

    private Context mContext;
    private ArrayList<Post> posts;

    public PostAdapter(Context context, ArrayList<Post> post) {
        super(context, 0,post);
        mContext = context;
        posts = post;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View postListView = convertView;
        if(postListView == null){
            postListView = LayoutInflater.from(mContext).inflate(R.layout.post,parent,false);
        }

        Post currentPost = (Post) getItem(position);

        ImageView image = postListView.findViewById(R.id.imgPostMedia);
        String url = currentPost.getMediaString();
        if(!url.equals("") && currentPost.getType().equals("twitter")) {
            url = addChar(url,'s',4);
            Picasso.get().load(url).error(R.drawable.ball_100x100).into(image);
        }else if(currentPost.getType().equals("instagram" ) && !url.equals("")){
            Picasso.get().load(url).error(R.drawable.ball_100x100).into(image);
        }else{
            image.setImageResource(0);
        }


        TextView body = postListView.findViewById(R.id.txtPostText);
        body.setText(currentPost.getBody());

        ImageView icon = postListView.findViewById(R.id.imgIcon);
        if(currentPost.getType().equals("instagram"))
            icon.setImageResource(R.drawable.ig_logo);
        else
            icon.setImageResource(R.drawable.twitter);

        return postListView;
    }

    public String addChar(String str, char ch, int position) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(position, ch);
        return sb.toString();
    }
}