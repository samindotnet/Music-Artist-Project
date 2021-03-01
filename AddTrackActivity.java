package com.example.musicartistproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTrackActivity extends AppCompatActivity {
    TextView textViewArtistName;
    EditText editTextTrackName;
    SeekBar seekBarRating;
    ListView listViewTracks;
    Button btnAddTrack;
    DatabaseReference databaseTracks;
    List<Track> tracks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);
        textViewArtistName=(TextView)findViewById(R.id.textViewArtistName);
        editTextTrackName=(EditText)findViewById(R.id.editTextName);
        seekBarRating=(SeekBar)findViewById(R.id.seekBarRating);
        listViewTracks =(ListView)findViewById(R.id.listViewTracks);
        btnAddTrack=(Button)findViewById(R.id.btnAddTrack);
        Intent intent = getIntent();
        tracks = new ArrayList<>();
        String id = intent.getStringExtra(MainActivity.Artist_ID);
        String name= intent.getStringExtra(MainActivity.Artist_Name);
        textViewArtistName.setText(name);
        databaseTracks= FirebaseDatabase.getInstance().getReference("tracks").child(id);
        btnAddTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTrack();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseTracks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tracks.clear();
                for(DataSnapshot trackSnapshot: snapshot.getChildren()){
                    Track track = trackSnapshot.getValue(Track.class);
                    tracks.add(track);
                }
                TrackList trackListAdapter = new TrackList(AddTrackActivity.this,tracks);
                listViewTracks.setAdapter(trackListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveTrack(){
        String trackName = editTextTrackName.getText().toString().trim();
        int rating = seekBarRating.getProgress();
        if(!TextUtils.isEmpty(trackName)){
            String id = databaseTracks.push().getKey();
            Track track = new Track(id,trackName,rating);
            databaseTracks.child(id).setValue(track);
            Toast.makeText(this,"Track Saved successfully",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"Track Name should not be empty",Toast.LENGTH_LONG).show();

        }
    }
}