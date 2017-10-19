package com.youphptube.youphptube;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MasterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<HashMap<String, String>> VideosList;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        VideosList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);


        final SwipeRefreshLayout RefreshLayout = (SwipeRefreshLayout) findViewById(R.id.RefreshLayout);

        RefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new GetVideos().execute();
                        RefreshLayout.setRefreshing(false);
                    }
                }
        );




        new GetVideos().execute();

    }

    private class GetVideos extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressBar;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(MasterActivity.this);
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

            String jsonStr = sh.GetVideos(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray videos = jsonObj.getJSONArray("videos");

                    // looping through All Contacts
                    for (int i = 0; i < videos.length(); i++) {
                        JSONObject c = videos.getJSONObject(i);

                        // tmp hash map for single contact
                        HashMap<String, String> video = new HashMap<>();

                        // adding each child node to HashMap key => value
                        video.put("VideoID", c.getString("id"));
                        video.put("name", c.getString("name"));
                        video.put("email", c.getString("email"));
                        video.put("photoURL", c.getString("photoURL"));
                        video.put("Thumbnail", c.getString("Thumbnail"));
                        video.put("duration", c.getString("duration"));
                        video.put("VideoUrl", c.getString("VideoUrl"));

                        video.put("title", c.getString("title"));
                        video.put("clean_title", c.getString("clean_title"));
                        video.put("description", c.getString("description"));
                        video.put("views_count", c.getString("views_count"));
                        video.put("created", c.getString("created"));
                        video.put("UserPhoto", c.getString("UserPhoto"));
                        VideosList.add(video);
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
                                "There was an error contacting the server",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            VideoAdaptor adapter=new VideoAdaptor(MasterActivity.this, VideosList, R.layout.video_list_normal);

            //ListAdapter adapter = new SimpleAdapter(context,ListaAccoes, R.layout.video_list_normal, new String[] { "Title"}, new int[] {R.id.NomeFicheiro});
            lv.setAdapter(adapter);
            lv.setClickable(true);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                    Object o = lv.getItemAtPosition(position);


                    String videourl= VideosList.get(position).get("VideoUrl");
                    String videopreviewurl= VideosList.get(position).get("Thumbnail");
                    String VideoID = VideosList.get(position).get("VideoID");
                    if (videourl!=null) {
                        Intent myIntent = new Intent(MasterActivity.this, VideoPlayer.class);
                        myIntent.putExtra("videourl", videourl);
                        myIntent.putExtra("videopreviewurl", videopreviewurl);
                        myIntent.putExtra("VideoID", VideoID);
                        startActivity(myIntent);
                    }


                }
            });

            progressBar.dismiss();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.master, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            SharedPreferences Defs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = Defs.edit();
            editor.putBoolean("AutoLogin", false);
            editor.apply();
            finish();
            Intent objIndent = new Intent(MasterActivity.this,ConfigurationActivity.class);
            startActivity(objIndent);
            return true;

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
