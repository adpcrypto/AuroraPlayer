package com.adpcrypto.auroraplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.adpcrypto.auroraplayer.classes.AudioFile;
import com.adpcrypto.auroraplayer.classes.AudioFileDatabase;
import com.adpcrypto.auroraplayer.classes.InternalStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    public static AudioFileDatabase audioFileDatabase;
    Context mContent;
    static List<AudioFile> audioFiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadDarkMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getSupportActionBar().hide();
        mContent = getApplicationContext();
    }



    private void requestPermission() {
        if((ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(StartActivity.this, "Storage permission is required", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
        }else{
            new LongOperation().execute();
        }
    }

    private void loadDarkMode() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        boolean isNightMode = sharedPreferences.getBoolean("dark", true);


        if(isNightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        //editor.putBoolean("dark",!isNightMode);
        //editor.apply();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (!(grantResults.length > 0)) {
                requestPermission();
                return;
            }
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                requestPermission();
                return;
            }
            new LongOperation().execute();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();
    }


    public static List<AudioFile> getAudioFiles(Context context){
        List<AudioFile> temp = new ArrayList<>();
        String[] projection={
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ARTIST,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE
        };
        HandlerThread backgroundHandlerThread = new HandlerThread("contentObserverThread");
        backgroundHandlerThread.start();
        context.getContentResolver().registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,true, new MyContentObserver(new Handler(backgroundHandlerThread.getLooper()), context));
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
        if(cursor !=null){
            while(cursor.moveToNext()){
                String path = cursor.getString(1);
                int slashLastIndex = path.lastIndexOf("/");
                String substr = path.substring(0,slashLastIndex);//Won't count last / only path of folder
                int index = substr.lastIndexOf("/");
                String folderName = substr.substring(index+1,slashLastIndex);
                String id = cursor.getString(0);
                List<AudioFile> temp1 = audioFileDatabase.audioFileDao().checkforExistID(id);
                AudioFile audioFile1;
                if(temp1.size() > 0){
                    audioFile1 = new AudioFile(id,cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)
                            ,temp1.get(0).getIsfav(),folderName,cursor.getString(5),cursor.getString(6)
                            ,cursor.getString(7),cursor.getString(8));
                }else {
                    audioFile1 = new AudioFile(id,cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)
                            ,false,folderName,cursor.getString(5),cursor.getString(6)
                            ,cursor.getString(7),cursor.getString(8));
                }
                temp.add( audioFile1);
            }
            cursor.close();
            backgroundHandlerThread.quitSafely();
            backgroundHandlerThread =null;
            audioFileDatabase.audioFileDao().clearAudioDatabase();
            for(int j=0;j<temp.size();j++) {
                audioFileDatabase.audioFileDao().addAudioFile(temp.get(j));
            }
        }

        try {
            Collections.sort(temp, (s1, s2) -> s1.getFileName().compareToIgnoreCase(s2.getFileName()));
            InternalStorage.writeObject(context,"my_key",temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }


    static class MyContentObserver extends ContentObserver {


        final Context mContent;
        public MyContentObserver(Handler handler,Context c) {
            super(handler);
            mContent =c;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new Thread(() -> getAudioFiles(mContent)).start();

        }
    }

    private class LongOperation extends AsyncTask<Void, Void, Void> {

        /*@Override
        protected String doInBackground(String... params) {
            audioFileDatabase = AudioFileDatabase.getDatabase(getApplicationContext());
            audioFiles = getAudioFiles(getApplicationContext());
        }
        @Override
        protected void onPostExecute(String result) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }*/



        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            audioFileDatabase = AudioFileDatabase.getDatabase(getApplicationContext());
            audioFiles = getAudioFiles(getApplicationContext());
            return null;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}