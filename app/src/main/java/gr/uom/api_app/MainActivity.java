package gr.uom.api_app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class MainActivity extends AppCompatActivity {

    private static int IMG_CODE = 100;

    private LoginButton loginButton;
    private Button btnPost,btnStory,btnTrends,btnSearchHashtags,btnUpload;
    private EditText postText,txtHashtag;
    private List<String> hashtagList;
    private Switch imgSwitch,fbSwitch,instaSwitch,twitterSwitch;
    private ImageView imageView;
    private ArrayAdapter<String> hashtagsAdapter;

    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private AccessToken accessToken,testSiteAccessToken;

    private String testSiteAccessTokenString = null;
    private String testSiteID, instaId;
    private String hashtagString,hashtagId,imgPath;
    private ArrayList<Post> posts;
    private static MainActivity mainActivity;
    private Bitmap mBitmap;

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
        mainActivity = this;

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        btnSearchHashtags = findViewById(R.id.btnSearchHashtag);
        btnTrends = findViewById(R.id.btnGetTrends);
        btnUpload = findViewById(R.id.btnUploadImg);
        txtHashtag = findViewById(R.id.txtHashtag);
        imageView = findViewById(R.id.imageView);
        btnPost = findViewById(R.id.btnPost);
        btnStory = findViewById(R.id.btnStory);
        postText = findViewById(R.id.postPlainText);
        imgSwitch = findViewById(R.id.imgSwitch);
        fbSwitch = findViewById(R.id.facebookSwitch);
        instaSwitch = findViewById(R.id.instaSwitch);
        twitterSwitch = findViewById(R.id.twitterSwitch);

        hashtagList = new ArrayList<>();
        posts = new ArrayList<>();

        loginButton = findViewById(R.id.login_button);

        imgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    btnUpload.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                }else{
                    btnUpload.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                }
            }
        });

        btnTrends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTrendingHashtags();
            }
        });

        btnSearchHashtags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchHashtag();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImg();
            }
        });

        loginButton.setPermissions("public_profile","email","pages_read_engagement",
                "pages_manage_posts");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();

                getCredentials();

                btnPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createPost();
                    }
                });

                btnStory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createStory();
                    }
                });

            }

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

    private void createStory() {

        if(fbSwitch.isChecked()) {
            if (imgSwitch.isChecked()) {

            } else{

            }
        }

        if(instaSwitch.isChecked() && imgSwitch.isChecked()){
            createInstagramIntent(imgPath);
        }

        if(twitterSwitch.isChecked()){
            if(imgSwitch.isChecked()){

            }else{

            }
        }

    }

    private void createInstagramIntent(String mediaPath){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType("image/*");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setPackage("com.instagram.android");

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to insta"));
    }

    private void uploadImg(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, IMG_CODE);
    }

    private void getTrendingHashtags(){

        GetTrends getTrends = new GetTrends(this);
        getTrends.execute();
        try {
            hashtagList = getTrends.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        hashtagsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hashtagList);
        ListView listView = findViewById(R.id.hashtagListView);
        listView.setAdapter(hashtagsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("stavros",hashtagList.get(position));
                igHashtagSearch(hashtagList.get(position));
                searchPosts(hashtagList.get(position));
            }
        });

    }

    private void igHashtagSearch(final String hashtag) {

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

                            searchPosts(hashtag);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("user_id", instaId);
        parameters.putString("q", hashtag);
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void searchHashtag() {

        final String hashtag = txtHashtag.getText().toString();
        SearchHashtagsTask search = new SearchHashtagsTask(this,hashtag);
        search.execute();

        try {
            hashtagList = search.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        hashtagsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hashtagList);
        ListView listView = findViewById(R.id.hashtagListView);
        listView.setAdapter(hashtagsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("stavros",hashtagList.get(position));
                igHashtagSearch(hashtagList.get(position));
            }
        });

    }

    private void searchPosts(String hashtag) {

        Intent intent = new Intent(MainActivity.this, PostsActivity.class);
        intent.putExtra("hashtag", hashtag);
        intent.putExtra("hashtagId",hashtagId);
        intent.putExtra("instaId",instaId);
        intent.putExtra("context", new ContextClass(this));
        startActivity(intent);
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

                            testSiteAccessToken = new AccessToken(testSiteAccessTokenString,
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

    private void createPost() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(getResources().getString(R.string.CONSUMER_KEY))
                .setOAuthConsumerSecret(getResources().getString(R.string.CONSUMER_SECRET))
                .setOAuthAccessToken(getResources().getString(R.string.ACCESS_TOKEN))
                .setOAuthAccessTokenSecret(getResources().getString(R.string.ACCESS_TOKEN_SECRET));

        String text = postText.getText().toString();

        if(fbSwitch.isChecked()) {
            if (imgSwitch.isChecked()) {

                GraphRequest request = null;
                try {
                    request = GraphRequest.newPostRequest(
                            testSiteAccessToken,
                            "/" + testSiteID + "/photos",
                            new JSONObject("{\"message\": \"" + text + "\"}"),
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    Toast.makeText(MainActivity.this, "Post Succeeded", Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Bitmap bi = BitmapFactory.decodeFile(imgPath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bi.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] data = stream.toByteArray();

                Bundle parameters = new Bundle();
                parameters.putByteArray("source", data);
                request.setParameters(parameters);
                request.executeAsync();

                //Picasso.get().load(new File(imgPath)).into(target);

            } else{

                GraphRequest request = null;
                try {
                    request = GraphRequest.newPostRequest(
                            testSiteAccessToken,
                            "/me/feed",
                            new JSONObject("{\"message\":\"" + text + "\"}"),
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    Toast.makeText(MainActivity.this, "Facebook post succeeded", Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                request.executeAsync();

            }
        }

        if(instaSwitch.isChecked() && imgSwitch.isChecked()){
            createInstagramIntent(imgPath);
        }

        if(twitterSwitch.isChecked()){
            if(imgSwitch.isChecked()){
                PostTweetTask postTweetTask = new PostTweetTask(this,text,imgPath);
                postTweetTask.execute();
            }else{
                AsyncTwitterFactory factory = new AsyncTwitterFactory(cb.build());
                AsyncTwitter asyncTwitter = factory.getInstance();
                asyncTwitter.updateStatus(text);
            }
            Toast.makeText(MainActivity.this, "Twitter post succeeded", Toast.LENGTH_SHORT).show();
        }

    }

    ;

    public static MainActivity getInstance() {
        return mainActivity;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode , resultCode , data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == IMG_CODE){
            Uri chosenImageUri = data.getData();

            mBitmap = null;
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageUri);
                imageView.setImageBitmap(mBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Uri tempUri = getImageUri(getApplicationContext(), mBitmap);

            Log.d("stavros", getRealPathFromURI(tempUri));
            imgPath = getRealPathFromURI(tempUri);
        }

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