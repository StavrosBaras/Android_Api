package gr.uom.api_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private Button btnSearch,btnPostText;
    private EditText postText,imgURL;
    private Switch imgSwitch;

    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private AccessToken accessToken,testSiteAccessToken;

    private String testSiteAccessTokenString = null;
    private String testSiteID, instaId;
    private String hashtagString,hashtagId;
    private ArrayList<Post> posts;

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            if(ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        btnSearch = findViewById(R.id.btnSearch);
        btnPostText = findViewById(R.id.btnPostText);
        postText = findViewById(R.id.postPlainText);
        imgURL = findViewById(R.id.imgURLPlainText);
        imgSwitch = findViewById(R.id.imgSwitch);
        posts = new ArrayList<>();

        loginButton = findViewById(R.id.login_button);

        imgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    imgURL.setVisibility(View.VISIBLE);
                }else{
                    imgURL.setVisibility(View.INVISIBLE);
                }
            }
        });

        loginButton.setPermissions("public_profile","email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();

                getCredentials();

                btnSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        igHashtagSearch();
                    }
                });

                createFbPost();

            }

                        /*Text Post
                        GraphRequest request = null;
                        try {
                            request = GraphRequest.newPostRequest(
                                    accessToken,
                                    "/"+ TestSiteID + "/feed",
                                    new JSONObject("{\"message\":\"Awesome!!!!\"}"),
                                    new GraphRequest.Callback() {
                                        @Override
                                        public void onCompleted(GraphResponse response) {
                                            Toast.makeText(MainActivity.this,"Post Succeded", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        request.executeAsync();*/

                       /* Img post
                        GraphRequest request = null;
                        try {
                            request = GraphRequest.newPostRequest(
                                    accessToken,
                                    "/" + TestSiteID + "/photos",
                                    new JSONObject("{\"url\":\"http://i.imgur.com/DvpvklR.png\",\"message\":\"HELLO THERE\"}"),
                                    new GraphRequest.Callback() {
                                        @Override
                                        public void onCompleted(GraphResponse response) {
                                            Toast.makeText(MainActivity.this,"Image uploaded...", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        request.executeAsync();
                    }
                });*/

                @Override
                public void onCancel() {
                        Toast.makeText(MainActivity.this, "Login attempt canceled...", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException e) {
                    Toast.makeText(MainActivity.this, "Login attempt failed...", Toast.LENGTH_SHORT).show();
                }
        });
    }

    private void igHashtagSearch() {

        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/ig_hashtag_search",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response.getJSONObject().getString("data"));
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            hashtagId = jsonObject.getString("id");

                            Log.d("Stavros", "hashtag id is: " + hashtagId);

                            igPostSearch();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("user_id", instaId);
        parameters.putString("q", "kingdomhearts");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void igPostSearch() {

        Intent intent = new Intent(MainActivity.this, PostsActivity.class);
        intent.putExtra("hashtag", "kingdomhearts");
        intent.putExtra("hashtagId",hashtagId);
        intent.putExtra("instaId",instaId);
        startActivity(intent);

        /*GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/" + hashtagId + "/top_media",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response.getJSONObject().getString("data"));

                            for (int i=0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                //Log.d("Stavros", "id is : " + jsonObject.getString("id") + " media url is : " + jsonObject.getString("media_url") +
                                        //" caption is : " + jsonObject.getString("caption"));

                                long id = Long.parseLong(jsonObject.getString("id"));
                                String media_type = jsonObject.getString("media_type");
                                String mediaString = "";
                                if(media_type.equals("IMAGE")) {
                                    mediaString = jsonObject.getString("media_url");
                                }
                                String body = jsonObject.getString("caption");
                                int comments_count = Integer.parseInt(jsonObject.getString("comments_count"));
                                int likes_count = Integer.parseInt(jsonObject.getString("like_count"));

                                posts.add(new Post(id,body,mediaString,comments_count,likes_count,"instagram"));

                                /*for(Post post : posts){
                                    Log.d("Stavros", "-------- " + mediaString + "  " + body);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,media_type,comments_count,like_count,media_url,caption");
        parameters.putString("user_id", instaId);
        request.setParameters(parameters);
        request.executeAsync();*/
    }

    private void getCredentials() {
        GraphRequest request1 = GraphRequest.newGraphPathRequest(
                accessToken,
                "/me/accounts",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response.getJSONObject().getString("data"));

                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            testSiteID = jsonObject.getString("id");
                            testSiteAccessTokenString = jsonObject.getString("access_token");

                            AccessToken siteAccessToken = new AccessToken(testSiteAccessTokenString,
                                    accessToken.getApplicationId(),
                                    accessToken.getUserId(),
                                    null, null, null, null, null, null, null);

                            Log.d("Stavros", "TEST SITE ID IS: " + testSiteID + "AND THE ACCESS TOKEN IS: " + testSiteAccessTokenString);
                            Toast.makeText(MainActivity.this, "Got facebook data...", Toast.LENGTH_SHORT).show();

                            getInstaId();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters1 = new Bundle();
        parameters1.putString("fields", "id,access_token");
        request1.setParameters(parameters1);
        request1.executeAsync();
    }

    private void getInstaId() {
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/" + testSiteID,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.getJSONObject().getString("instagram_business_account"));
                            instaId = jsonObject.getString("id");

                            Log.d("Stavros", "INSTAGRAM ID IS: " + instaId);
                            Toast.makeText(MainActivity.this, "Got instagram data...", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "instagram_business_account");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void createFbPost() {


        btnPostText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = postText.getText().toString();
                String url = imgURL.getText().toString();

                if (imgSwitch.isChecked()) {
                    GraphRequest request = null;
                    try {
                        request = GraphRequest.newPostRequest(
                                testSiteAccessToken,
                                "/" + testSiteID + "/photos",
                                new JSONObject("{\"url\": \"" + url + "\",\"message\": \"" + text + "\"}"),
                                new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse response) {
                                        Toast.makeText(MainActivity.this, "Post Succeeded", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    request.executeAsync();
                } else {
                    GraphRequest request = null;
                    try {
                        request = GraphRequest.newPostRequest(
                                testSiteAccessToken,
                                "/"+ testSiteID + "/feed",
                                new JSONObject("{\"message\":\"" + text + "\"}"),
                                new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse response) {
                                        Toast.makeText(MainActivity.this,"Post Succeeded", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    request.executeAsync();
                }
            }
        });
    }

    ;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode , resultCode , data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        /*if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_VIDEO_CODE){

                Uri selectedVideo = data.getData();

                ShareVideo video = new ShareVideo.Builder()
                        .setLocalUrl(selectedVideo)
                        .build();

                ShareVideoContent videoContent = new ShareVideoContent.Builder()
                        .setContentTitle("This is a useful video")
                        .setContentDescription("Yep that's a video alright!")
                        .setVideo(video)
                        .build();

                if(shareDialog.canShow(ShareVideoContent.class))
                    shareDialog.show(videoContent);
            }
        }*/

    }

    /*-------------------------------------------------------------------------------
    CallbackManager callbackManager;
    ProgressDialog mDialog;
    ImageView imgAvatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgAvatar = (ImageView)findViewById(R.id.imageAvatar);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile","email","user_birthday"));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setMessage("Retrieving data...");
                mDialog.show();

                String accessToken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        mDialog.dismiss();

                        Log.d("response",response.toString());

                        getData(object);
                    }
                });
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

    }

    private void getData(JSONObject object) {
        try {
            URL profile_picture = new URL ("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");

            Picasso.get().load(profile_picture.toString()).into(imgAvatar);

            Log.d("FB",object.getString("email"));
            Log.d("FB",object.getString("user_birthday"));


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }*/
}