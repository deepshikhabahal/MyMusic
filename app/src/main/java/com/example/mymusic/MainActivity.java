package com.example.mymusic;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView= findViewById(R.id.listView);
        //To give access
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this, "Runtime Permission Given", Toast.LENGTH_SHORT).show();
                        //Environment = gives external storage directory ,ie, all the songs will come in array list
                        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                        String[] items = new String[mySongs.size()];
                        for (int i=0; i<mySongs.size();i++){
                            items[i] = mySongs.get(i).getName().replace(".mp3","");
                        }
                        //to show the contents in listView ,we use Adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);
                        listView.setAdapter(adapter);
                        //We click the button and go into another activity
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(MainActivity.this, PlaySong.class);
                                    String currentSong = listView.getItemAtPosition(position).toString();
                                    intent.putExtra("songList", mySongs);
                                    intent.putExtra("currentSong", currentSong);
                                    intent.putExtra("position", position);
                                    //To start the activity
                                    startActivity(intent);
                                }
                            });

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                            @Override
                           public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    //Directory of SD card is file
    //File is an abstract implementation
     public ArrayList<File> fetchSongs(File file){    //method :fetchSongs
        //we give the directory(URL) and it will fetch all the songs into the ArrayList
         ArrayList arrayList = new ArrayList();
          //lists all the files into the present directory
         File[] songs = file.listFiles();
         //recursive implementation
         if(songs !=null){
             //all the fetched songs are iterated
             for(File myFile: songs){
                 if(!myFile.isHidden() && myFile.isDirectory()){
                     //add all the songs in the arraylist which are in the directory
                     arrayList.addAll(fetchSongs(myFile));
                 }
                 else{
                     if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".") ){
                         arrayList.add(myFile);
                     }
                 }

             }
         }
         return arrayList;


     }

}