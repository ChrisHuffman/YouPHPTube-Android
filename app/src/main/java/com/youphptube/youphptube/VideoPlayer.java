package com.youphptube.youphptube;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class VideoPlayer extends AppCompatActivity {
    ArrayList<HashMap<String, String>> VideosList;
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_player);

        VideosList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);


        Intent intent = getIntent();
        String videourl = intent.getStringExtra("videourl");
        String videopreviewurl = intent.getStringExtra("videopreviewurl");
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






            new GetVideos().execute();
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

        private ProgressDialog progressBar;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();
            progressBar = new ProgressDialog(VideoPlayer.this);
            progressBar.setCancelable(true);
            progressBar.setMessage(getString(R.string.pleasewait));
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            SharedPreferences Defs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String ServerUrl = Defs.getString("ServerUrl", "");
            String url = ServerUrl + "/videosAndroid.json";

            String jsonStr = sh.GetVideo(url);

            //Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("videos");

                    /*// looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", c.getString("id"));
                        contact.put("name", c.getString("name"));
                        contact.put("email", c.getString("email"));
                        contact.put("photoURL", c.getString("photoURL"));
                        contact.put("Thumbnail", c.getString("Thumbnail"));
                        contact.put("duration", c.getString("duration"));
                        contact.put("VideoUrl", c.getString("VideoUrl"));

                        contact.put("title", c.getString("title"));
                        contact.put("clean_title", c.getString("clean_title"));
                        contact.put("description", c.getString("description"));
                        contact.put("views_count", c.getString("views_count"));
                        contact.put("created", c.getString("created"));
                        contact.put("UserPhoto", c.getString("UserPhoto"));
                        VideosList.add(contact);
                    }*/
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

            progressBar.dismiss();
        }
    }

    private class GetVideos extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressBar;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();
            /*progressBar = new ProgressDialog(VideoPlayer.this);
            progressBar.setCancelable(true);
            progressBar.setMessage(getString(R.string.pleasewait));
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();*/
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            SharedPreferences Defs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String ServerUrl = Defs.getString("ServerUrl", "");
            String url = ServerUrl + "/videosAndroid.json";

            String jsonStr = sh.GetVideos(url);

            //Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("videos");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", c.getString("id"));
                        contact.put("name", c.getString("name"));
                        contact.put("email", c.getString("email"));
                        contact.put("photoURL", c.getString("photoURL"));
                        contact.put("Thumbnail", c.getString("Thumbnail"));
                        contact.put("duration", c.getString("duration"));
                        contact.put("VideoUrl", c.getString("VideoUrl"));

                        contact.put("title", c.getString("title"));
                        contact.put("clean_title", c.getString("clean_title"));
                        contact.put("description", c.getString("description"));
                        contact.put("views_count", c.getString("views_count"));
                        contact.put("created", c.getString("created"));
                        contact.put("UserPhoto", c.getString("UserPhoto"));
                        VideosList.add(contact);
                    }
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

            VideoAdaptor adapter=new VideoAdaptor(VideoPlayer.this, VideosList, R.layout.video_list_horizontal);

            //ListAdapter adapter = new SimpleAdapter(context,ListaAccoes, R.layout.video_list_normal, new String[] { "Title"}, new int[] {R.id.NomeFicheiro});
            lv.setAdapter(adapter);
            lv.setClickable(true);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                    Object o = lv.getItemAtPosition(position);


                    String videourl= VideosList.get(position).get("VideoUrl");
                    String videopreviewurl= VideosList.get(position).get("Thumbnail");
                    if (videourl!=null) {
                        Intent myIntent = new Intent(VideoPlayer.this, VideoPlayer.class);
                        myIntent.putExtra("videourl", videourl);
                        myIntent.putExtra("videopreviewurl", videopreviewurl);
                        startActivity(myIntent);
                        finish();
                    }


                }
            });

            /*ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
                    R.layout.video_list_normal, new String[]{ "title","mobile"},
                    new int[]{R.id.email, R.id.mobile});
            lv.setAdapter(adapter);*/
            //progressBar.dismiss();
        }
    }
}


