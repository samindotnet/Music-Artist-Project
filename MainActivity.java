package com.example.musicartistproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String Artist_Name ="artistname";
    public static final String Artist_ID="artistid";
    EditText txtName;
    Button btnAdd;
    Spinner spinnerGenres;
    DatabaseReference databaseArtists;
    ListView listViewArtists;
    List<Artist> artists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseArtists = FirebaseDatabase.getInstance().getReference("artists");
        txtName = (EditText)findViewById(R.id.editTextName);
        btnAdd = (Button)findViewById(R.id.btnAddArtist);
        spinnerGenres=(Spinner)findViewById(R.id.spinnerGenres);
        listViewArtists =(ListView)findViewById(R.id.listViewArtists);
        artists = new ArrayList<>();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtist();
            }
        });
        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                Artist artist = artists.get(i);
                Intent intent = new Intent(getApplicationContext(),AddTrackActivity.class);
                intent.putExtra(Artist_ID,artist.getArtistId());
                intent.putExtra(Artist_Name,artist.getArtistName());
                startActivity(intent);
            }
        });
        listViewArtists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = artists.get(position);
                showUpdateDialog(artist.getArtistId(),artist.getArtistName());
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                artists.clear();
                for(DataSnapshot artistSnapshot : dataSnapshot.getChildren()){
                    Artist artist = artistSnapshot.getValue(Artist.class);
                    artists.add(artist);
                }
                ArtistList adapter = new ArtistList(MainActivity.this,artists);
                listViewArtists.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showUpdateDialog(String artist_ID,String artist_Name){
        AlertDialog.Builder dialogBuilder =new AlertDialog.Builder(this);
        LayoutInflater inflater =getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog,null);
        dialogBuilder.setView(dialogView);
        final EditText editTextName = (EditText)dialogView.findViewById(R.id.editText);
        final Spinner spinnerGenres = (Spinner)dialogView.findViewById(R.id.spinnergenres);
        final Button btnUpdate = (Button)dialogView.findViewById(R.id.btnUpdate);
        dialogBuilder.setTitle("Updating Artist"+artist_ID);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenres.getSelectedItem().toString();
                if(TextUtils.isEmpty(name)){
                    editTextName.setError("Name Required");
                    return;
                }
                updateArtist(artist_ID,name,genre);
                alertDialog.dismiss();
            }
        });
    }
    private void addArtist(){
        String name = txtName.getText().toString().trim();
        String genre= spinnerGenres.getSelectedItem().toString();
        if(!TextUtils.isEmpty(name)){
            String id =databaseArtists.push().getKey();
            Artist artist = new Artist(id,name,genre);
            databaseArtists.child(id).setValue(artist);
            Toast.makeText(this,"Artist Added",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "You should enter a name", Toast.LENGTH_LONG).show();
        }
    }
    private boolean updateArtist(String id,String name, String genre){
        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("artists").child(id);
        Artist artist = new Artist(id,name,genre);
        databaseReference.setValue(artist);
        Toast.makeText(this,"Artist Updated Successfully",Toast.LENGTH_LONG).show();
        return true;
    }
}