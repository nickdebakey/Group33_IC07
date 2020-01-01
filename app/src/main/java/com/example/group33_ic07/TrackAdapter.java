package com.example.group33_ic07;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TrackAdapter extends ArrayAdapter<Track> {
    TextView tvName, tvAlbum, tvArtist, tvDate;

    public TrackAdapter(@NonNull Context context, int resource, @NonNull List<Track> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Track track = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.track, parent, false);
        }

        tvName = convertView.findViewById(R.id.tvName);
        tvAlbum = convertView.findViewById(R.id.tvAlbum);
        tvArtist = convertView.findViewById(R.id.tvArtist);
        tvDate = convertView.findViewById(R.id.tvDate);

        tvName.setText(track.name);
        tvAlbum.setText(track.album);
        tvArtist.setText(track.artist);
        tvDate.setText(track.updated);

        return convertView;
    }
}
