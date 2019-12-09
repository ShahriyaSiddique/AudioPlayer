package com.example.audioplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class AudioPlayerActivity extends AppCompatActivity {


    private RelativeLayout parentRelativeLayout,upperRelativeLayout;
    private LinearLayout lowerLinearLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String store = "";

    private ImageView playPauseBtn,previousBtn,nextBtn,posterOfAudio;
    private TextView audioNameTV;
    private Button voiceOnOffBtn;
    private FloatingActionButton input;

    private String mode = "on";

    private MediaPlayer audioPlayer;
    private int position;
    private ArrayList<File> audioFiles;
    private String audioName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_player_activity);

        voiceCommandPermission();

        playPauseBtn = findViewById(R.id.play_pause_btn);
        previousBtn= findViewById(R.id.previous_btn);
        nextBtn = findViewById(R.id.next_btn);
        voiceOnOffBtn = findViewById(R.id.voice_enable_btn);
        posterOfAudio = findViewById(R.id.audio_poster);
        lowerLinearLayout = findViewById(R.id.lower_linear_layout);
        upperRelativeLayout = findViewById(R.id.upper_relative_layout);
        audioNameTV= findViewById(R.id.audio_name);
        input= findViewById(R.id.input);


        parentRelativeLayout = findViewById(R.id.parent_relative_layout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(AudioPlayerActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        validateReceiveFilesThenPlay();

        posterOfAudio.setBackgroundResource(R.drawable.logo);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle)
            {
                ArrayList<String> matchFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matchFound!=null)
                {
                    if (mode.equals("on"))
                    {
                        store = matchFound.get(0);

                        if (store.equals("pause audio") || store.equals("pause"))
                        {
                            playPauseAudio();
                            Toast.makeText(AudioPlayerActivity.this, "Command = "+ store, Toast.LENGTH_SHORT).show();

                        }
                        else if (store.equals("play audio") || store.equals("play"))
                        {
                            playPauseAudio();
                            Toast.makeText(AudioPlayerActivity.this, "Command = "+ store, Toast.LENGTH_SHORT).show();

                        }
                        else if (store.equals("next audio") || store.equals("next"))
                        {
                            playNextAudio();
                            Toast.makeText(AudioPlayerActivity.this, "Command = "+ store, Toast.LENGTH_SHORT).show();

                        }
                        else if (store.equals("previous audio") || store.equals("previous"))
                        {
                            playPreviousAudio();
                            Toast.makeText(AudioPlayerActivity.this, "Command = "+ store, Toast.LENGTH_SHORT).show();

                        }


                    }
                }


            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });


        input.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {

                speechRecognizer.startListening(speechRecognizerIntent);
                store = "";
                /*switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:

                        speechRecognizer.startListening(speechRecognizerIntent);
                        store = "";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;

                }*/
                return false;
            }
        });


        voiceOnOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode.equals("on"))
                {
                    mode = "off";
                    voiceOnOffBtn.setText(R.string.voice_mode_off);
                    lowerLinearLayout.setVisibility(View.VISIBLE);
                    input.hide();

                }
                else
                {
                    mode = "on";
                    voiceOnOffBtn.setText(R.string.voice_mode_on);
                    lowerLinearLayout.setVisibility(View.GONE);
                    input.show();

                }
            }
        });


        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPauseAudio();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(audioPlayer.getCurrentPosition()>0)
                {
                    playNextAudio();
                }
            }
        });
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(audioPlayer.getCurrentPosition()>0)
                {
                    playPreviousAudio();
                }
            }
        });



    }

    private void validateReceiveFilesThenPlay()
    {
        if (audioPlayer!=null)
        {
            audioPlayer.start();
            audioPlayer.release();
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        audioFiles = (ArrayList) bundle.getParcelableArrayList("audio");
        audioName = audioFiles.get(position).getName();
        String audioNameReceive =intent.getStringExtra("name");

        audioNameTV.setText(audioNameReceive);
        audioNameTV.setSelected(true);

        position = bundle.getInt("position",0);
        Uri uri = Uri.parse(audioFiles.get(position).toString());

        audioPlayer = MediaPlayer.create(AudioPlayerActivity.this,uri);
        audioPlayer.start();
    }

    private void voiceCommandPermission()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (!(ContextCompat.checkSelfPermission(AudioPlayerActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED))
            {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();
            }
        }


    }

    private void playPauseAudio()
    {
        posterOfAudio.setBackgroundResource(R.drawable.four);

        if (audioPlayer.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.play);
            audioPlayer.pause();
        }
        else
        {
            playPauseBtn.setImageResource(R.drawable.pause);
            audioPlayer.start();

            posterOfAudio.setBackgroundResource(R.drawable.five);
        }
    }

    private void playNextAudio()
    {
        audioPlayer.pause();
        audioPlayer.stop();
        audioPlayer.release();

        position= ((position+1)%audioFiles.size());

        Uri uri = Uri.parse(audioFiles.get(position).toString());

        audioPlayer = MediaPlayer.create(AudioPlayerActivity.this,uri);
        audioName = audioFiles.get(position).toString();

        audioNameTV.setText(audioName);

        audioPlayer.start();
        posterOfAudio.setImageResource(R.drawable.three);

        if (audioPlayer.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.pause);
        }
        else
        {
            playPauseBtn.setImageResource(R.drawable.play);

            posterOfAudio.setBackgroundResource(R.drawable.five);
        }


    }

    private void playPreviousAudio()
    {
        audioPlayer.pause();
        audioPlayer.stop();
        audioPlayer.release();

        position= ((position-1)<0 ? (audioFiles.size()-1) : (position-1));

        Uri uri = Uri.parse(audioFiles.get(position).toString());
        audioPlayer = MediaPlayer.create(AudioPlayerActivity.this,uri);


        audioName = audioFiles.get(position).toString();
        audioNameTV.setText(audioName);
        audioPlayer.start();

        posterOfAudio.setImageResource(R.drawable.two);

        if (audioPlayer.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.pause);
        }
        else
        {
            playPauseBtn.setImageResource(R.drawable.play);

            posterOfAudio.setBackgroundResource(R.drawable.five);
        }


    }


}
