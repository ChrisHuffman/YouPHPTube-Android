package com.youphptube.youphptube;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class VideoPlayer extends AppCompatActivity {
    ArrayList<HashMap<String, String>> RelatedVideosList;
    private ListView RelatedVideosListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_player);

        RelatedVideosList = new ArrayList<>();
        RelatedVideosListView = (ListView) findViewById(R.id.list);


        Intent intent = getIntent();
        String videourl = intent.getStringExtra("videourl");
        String videopreviewurl = intent.getStringExtra("videopreviewurl");
        String VideoID = intent.getStringExtra("VideoID");
        if (videourl!=null){

            Uri uri= Uri.parse(videourl);
            VideoView video = (VideoView) findViewById(R.id.videoplayler);
            video.setVideoURI(uri);
            video.start();
            MediaController controller = new MediaController(this);
            controller.setAnchorView(video);
            controller.setMediaPlayer(video);
            controller.setAnchorView(video);
            video.setMediaController(controller);

            //Getting video information and add view count
            //In the future only videoID or URL will be passed and all video information will be get from here
            new GetVideo(VideoID).execute();

            //Getting related videos list
            new GetRelatedVideos().execute();
        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }


    }

    private class GetVideo extends AsyncTask<Void, Void, Void> {
        private String VideoID;
        private GetVideo(String videoid){
            VideoID = videoid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            SharedPreferences Defs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String ServerUrl = Defs.getString("ServerUrl", "");
            String url = ServerUrl + "/videoAndroid.json";

            String jsonStr = sh.GetVideo(url, VideoID);

            //Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    final String likes= jsonObj.getString("likes");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Likes: " + likes,
                                    Toast.LENGTH_LONG).show();
                        }
                    });



                } catch (final JSONException e) {
                    //Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                //Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class GetRelatedVideos extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            SharedPreferences Defs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String ServerUrl = Defs.getString("ServerUrl", "");
            String url = ServerUrl + "/videosAndroid.json";
            String jsonStr = sh.GetVideos(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray Videos = jsonObj.getJSONArray("videos");
                    // looping through All Videos
                    for (int i = 0; i < Videos.length(); i++) {
                        JSONObject c = Videos.getJSONObject(i);
                        // tmp hash map for single contact
                        HashMap<String, String> Video = new HashMap<>();
                        // adding each child node to HashMap key => value
                        Video.put("VideoID", c.getString("id"));
                        Video.put("name", c.getString("name"));
                        Video.put("email", c.getString("email"));
                        Video.put("photoURL", c.getString("photoURL"));
                        Video.put("Thumbnail", c.getString("Thumbnail"));
                        Video.put("duration", c.getString("duration"));
                        Video.put("VideoUrl", c.getString("VideoUrl"));
                        Video.put("title", c.getString("title"));
                        Video.put("clean_title", c.getString("clean_title"));
                        Video.put("description", c.getString("description"));
                        Video.put("views_count", c.getString("views_count"));
                        Video.put("created", c.getString("created"));
                        Video.put("UserPhoto", c.getString("UserPhoto"));
                        RelatedVideosList.add(Video);
                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "The has an error connecting to server: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "The has an error connecting to server",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            VideoAdaptor adapter=new VideoAdaptor(VideoPlayer.this, RelatedVideosList, R.layout.video_list_horizontal);
            RelatedVideosListView.setAdapter(adapter);
            RelatedVideosListView.setClickable(true);
            RelatedVideosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    String videourl= RelatedVideosList.get(position).get("VideoUrl");
                    String videopreviewurl= RelatedVideosList.get(position).get("Thumbnail");
                    String VideoID = RelatedVideosList.get(position).get("VideoID");
                    if (videourl!=null) {
                        Intent myIntent = new Intent(VideoPlayer.this, VideoPlayer.class);
                        myIntent.putExtra("videourl", videourl);
                        myIntent.putExtra("videopreviewurl", videopreviewurl);
                        myIntent.putExtra("VideoID", VideoID);
                        startActivity(myIntent);
                        finish();
                    }
                }
            });
        }
    }
}


