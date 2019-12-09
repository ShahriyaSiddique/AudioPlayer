package com.example.audioplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String[] items;
    private ListView audioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioList = findViewById(R.id.audioLists);

        externalStoragePermission();
    }

    public void externalStoragePermission()
    {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        displayAudioName();

                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response)
                    {


                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {
                        token.continuePermissionRequest();


                    }
                }).check();
    }

    public ArrayList<File> readAudioFile (File file)
    {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] allFiles = file.listFiles();

        for (File eachFile: allFiles)
        {
            if(eachFile.isDirectory() && !eachFile.isHidden())
            {
                arrayList.addAll(readAudioFile(eachFile));
            }
            else
            {
                if (eachFile.getName().endsWith(".mp3") || eachFile.getName().endsWith(".wav"))
                {
                    arrayList.add(eachFile);
                }
            }
        }

        return arrayList;

    }

    private void displayAudioName()
    {
        final ArrayList<File> audioFiles = readAudioFile(Environment.getExternalStorageDirectory());

        items = new String[audioFiles.size()];
        for (int counter=0; counter<audioFiles.size();counter++)
        {
            items[counter] = audioFiles.get(counter).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,items);
        audioList.setAdapter(adapter);

        audioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String audioName = audioList.getItemAtPosition(i).toString();

                Intent intent = new Intent(MainActivity.this,AudioPlayerActivity.class);
                intent.putExtra("audio",audioFiles);
                intent.putExtra("name",audioName);
                intent.putExtra("position",i);
                startActivity(intent);


            }
        });

    }


}
