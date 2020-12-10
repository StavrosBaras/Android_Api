package gr.uom.api_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
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
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_VIDEO_CODE = 1000;
    private LoginButton loginButton;
    private ImageView profileImg;
    private Button btnShareLink,btnSharePhoto,btnShareVideo,btnPostText;
    private EditText postText,imgURL;
    private Switch imgSwitch;

    private ShareDialog shareDialog;
    private CallbackManager callbackManager;

    private String TestSiteAccessToken  = null;
    private String TestSiteID;

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

        btnShareLink = (Button)findViewById(R.id.btnShareLink);
        btnSharePhoto = (Button)findViewById(R.id.btnSharePhoto);
        btnShareVideo = (Button)findViewById(R.id.btnShareVideo);
        btnPostText = (Button)findViewById(R.id.btnPostText);
        postText = findViewById(R.id.postPlainText);
        imgURL = findViewById(R.id.imgURLPlainText);
        imgSwitch = findViewById(R.id.imgSwitch);

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

        loginButton = (LoginButton)findViewById(R.id.login_button);
        profileImg = (ImageView)findViewById(R.id.profileImg);
        final String url = "https://platform-lookaside.fbsbx.com/platform/profilepic/?asid=2195148570630131&height=50&width=50&ext=1610107721&hash=AeS4oGDLb_GEvC492ag";

        //Picasso.get().load(url).into(profileImg);

        btnShareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Create callback
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(MainActivity.this,"Share successful...",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this,"Share unsuccessful...",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, error.getMessage() ,Toast.LENGTH_SHORT).show();
                    }
                });

                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setQuote("This is a useful link")
                        .setContentUrl(Uri.parse("https://youtube.com"))
                        .build();
                if(ShareDialog.canShow(ShareLinkContent.class)){
                    shareDialog.show(linkContent);
                }
            }
        });

        btnSharePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Create callback
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(MainActivity.this,"Share successful...",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this,"Share unsuccessful...",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, error.getMessage() ,Toast.LENGTH_SHORT).show();
                    }
                });

                Picasso.get().load(url).into(target);
            }
        });

        btnShareVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Choose Video dialog
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select video"), REQUEST_VIDEO_CODE);

            }
        });

        loginButton.setPermissions("public_profile","email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequest request1 = GraphRequest.newGraphPathRequest(
                        loginResult.getAccessToken(),
                        "/me/accounts",
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.getJSONObject().getString("data"));

                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    TestSiteID = jsonObject.getString("id");
                                    TestSiteAccessToken = jsonObject.getString("access_token");
                                    Log.d("Stavros", "TEST SITE ID IS: " + TestSiteID + "AND THE ACCESS TOKEN IS: " + TestSiteAccessToken);
                                    Toast.makeText(MainActivity.this, "Got data...", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters1 = new Bundle();
                parameters1.putString("fields", "id,access_token");
                request1.setParameters(parameters1);
                request1.executeAsync();

                btnPostText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AccessToken accessToken = new AccessToken(TestSiteAccessToken,
                                loginResult.getAccessToken().getApplicationId(),
                                loginResult.getAccessToken().getUserId(),
                                null, null, null, null, null, null, null);

                        String text = postText.getText().toString();
                        String url = imgURL.getText().toString();

                        if (imgSwitch.isChecked()) {
                            GraphRequest request = null;
                            try {
                                request = GraphRequest.newPostRequest(
                                        accessToken,
                                        "/" + TestSiteID + "/photos",
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
                                        accessToken,
                                        "/"+ TestSiteID + "/feed",
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

                    //------------------------------------------------------------------------
                /*info.setText("User ID: " + loginResult.getAccessToken().getUserId() + "\n");
                String imageURL = "https://graph.facebook.com/" + loginResult.getAccessToken().getUserId() + "/picture";

                AccessToken accessToken = loginResult.getAccessToken();

                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        getData(object);

                        Log.d("dataa",object.toString());

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,birthday");
                request.setParameters(parameters);
                request.executeAsync();*/

                @Override
                public void onCancel() {
                        Toast.makeText(MainActivity.this, "Login attempt canceled...", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException e) {
                    Toast.makeText(MainActivity.this, "Login attempt failed...", Toast.LENGTH_SHORT).show();
                }
        });
    };

    private void getData(JSONObject object) {
        try {
            URL profile_picture = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode , resultCode , data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
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