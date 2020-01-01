package com.example.group33_ic07;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText etSearch;
    TextView tvLimit, tvSort, tvResults;
    SeekBar sbLimit;
    Button bSearch;
    RadioGroup rgSort;
    RadioButton rbTrack, rbArtist;
    ListView lvTracks;
    ProgressBar pbLoading;

    String keyword;
    int limit;
    int sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("MusixMatch Track Search");

        etSearch = findViewById(R.id.etSearch);
        tvLimit = findViewById(R.id.tvLimit);
        tvSort = findViewById(R.id.tvSort);
        tvResults = findViewById(R.id.tvResults);
        sbLimit = findViewById(R.id.sbLimit);
        bSearch = findViewById(R.id.bSearch);
        rgSort = findViewById(R.id.rgSort);
        rbTrack = findViewById(R.id.rbTrack);
        rbArtist = findViewById(R.id.rbArtist);
        lvTracks = findViewById(R.id.lvTracks);
        pbLoading = findViewById(R.id.pbLoading);

        sbLimit.setProgress(0);
        sbLimit.setMax(20);
        rbTrack.setChecked(true);

        rgSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                sort = rgSort.indexOfChild(findViewById(rgSort.getCheckedRadioButtonId()));
                new GetResults().execute("http://api.musixmatch.com/ws/1.1/track.search?apikey=ac01025b59b44d00487c2eed6db6a294");
            }
        });

        sbLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvLimit.setText("Limit: " + (sbLimit.getProgress() + 5));
                limit = sbLimit.getProgress() + 5;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnectedOnline()) {
                    keyword = etSearch.getText().toString();
                    new GetResults().execute("http://api.musixmatch.com/ws/1.1/track.search?apikey=ac01025b59b44d00487c2eed6db6a294");
                }
                else {
                    Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isConnectedOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public class GetResults extends AsyncTask<String, Void, ArrayList<Track>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Track> doInBackground(String... strings) {
            ArrayList<Track> tracks = new ArrayList<>();
            StringBuilder params = new StringBuilder(strings[0]);

            if (sort == 0) {
                params.append("&q=" + keyword + "&page_size=" + limit + "&s_track_rating=desc");
            }
            else if (sort == 1) {
                params.append("&q=" + keyword + "&page_size=" + limit + "&s_artist_rating=desc");
            }

            try {
                URL url = new URL(params.toString());
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                JSONObject jsonObject = new JSONObject(IOUtils.toString(connection.getInputStream(), "UTF8"));
                JSONObject jsonMessage = jsonObject.getJSONObject("message");
                JSONObject jsonBody = jsonMessage.getJSONObject("body");
                JSONArray jsonTrackList = jsonBody.getJSONArray("track_list");

                for (int i = 0; i < jsonTrackList.length(); i++) {
                    JSONObject jsonTrackNumber = jsonTrackList.getJSONObject(i);
                    JSONObject jsonTrack = jsonTrackNumber.getJSONObject("track");

                    String date = jsonTrack.getString("updated_time");
                    String[] split1 = date.split("T");
                    String[] split2 = split1[0].split("-");
                    date = split2[1] + "-" + split2[2] + "-" + split2[0];

                    Track track = new Track(jsonTrack.getString("track_name"),
                                            jsonTrack.getString("album_name"),
                                            jsonTrack.getString("artist_name"),
                                            date,
                                            jsonTrack.getString("track_edit_url"));
                    tracks.add(track);

                    Log.d("demo", track.toString());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return tracks;
        }

        @Override
        protected void onPostExecute(final ArrayList<Track> tracks) {
            super.onPostExecute(tracks);
            pbLoading.setVisibility(View.INVISIBLE);

            TrackAdapter trackAdapter = new TrackAdapter(MainActivity.this, R.layout.track, tracks);
            lvTracks.setAdapter(trackAdapter);
            lvTracks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(tracks.get(i).url));
                    startActivity(intent);
                }
            });
        }
    }
}
