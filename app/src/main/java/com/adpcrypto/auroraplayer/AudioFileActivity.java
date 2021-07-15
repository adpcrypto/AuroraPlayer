package com.adpcrypto.auroraplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.adpcrypto.auroraplayer.classes.AudioFile;
import com.adpcrypto.auroraplayer.classes.InternalStorage;
import com.adpcrypto.auroraplayer.classes.MyLinearLayoutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.adpcrypto.auroraplayer.StartActivity.audioFileDatabase;

public class AudioFileActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String folder,album;
    AudioFileAdapter videoFileAdapter;
    ImageButton searchbuttontop;
    SwipeRefreshLayout swipeRefreshLayout;
    List<AudioFile> fileitems;
    Observer<List<AudioFile>> observer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        swipeRefreshLayout = findViewById(R.id.swipe_video_file);

        recyclerView = findViewById(R.id.file_rcv);
        folder = getIntent().getStringExtra("foldername");
        album = getIntent().getStringExtra("album");

        List<AudioFile> torem = new ArrayList<>();

        if(folder!=null && album ==null){
            try {
                fileitems = (List<AudioFile>) InternalStorage.readObject(getApplicationContext(),"my_key");
                for(AudioFile aud:fileitems){
                    if(!aud.getFolder().equals(folder)){
                        torem.add(aud);
                    }
                }
                fileitems.removeAll(torem);
            } catch (IOException | ClassNotFoundException e) {
                fileitems = new ArrayList<>();
                e.printStackTrace();
            }
        }else if(album!=null && folder ==null){
            try {
                fileitems = (List<AudioFile>) InternalStorage.readObject(getApplicationContext(),"my_key");
                for(AudioFile aud:fileitems){
                    if(!aud.getAlbum().equals(album)){
                        torem.add(aud);
                    }
                }
                fileitems.removeAll(torem);
            } catch (IOException | ClassNotFoundException e) {
                fileitems = new ArrayList<>();
                e.printStackTrace();
            }
        }



        if(folder!=null && album ==null){
            videoFileAdapter = new AudioFileAdapter(AudioFileActivity.this, fileitems,"folder");
        }else if(album!=null && folder ==null){
            videoFileAdapter = new AudioFileAdapter(AudioFileActivity.this, fileitems,"album");
        }

        recyclerView.setAdapter(videoFileAdapter);
        recyclerView.setLayoutManager(new MyLinearLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(50);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);




        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_actionbar);
        View view = this.getSupportActionBar().getCustomView();
        searchbuttontop = view.findViewById(R.id.search_button_top);




        //new Thread(() -> fileitems = audioFileDatabase.audioFileDao().selectByFolder(folder).getValue()).start();

        if(folder!=null && album ==null){
            observer = (Observer<List<AudioFile>>) audioFiles ->
                    new Thread(()->{
                        if(audioFiles.size()>0) {
                                videoFileAdapter.setItems(audioFiles);
                        }else{
                            finish();
                            return;
                        }

                    }).start();
            audioFileDatabase.audioFileDao().selectByFolder(folder).observe(this, observer);
        }else if(album!=null && folder ==null){
            observer = (Observer<List<AudioFile>>) audioFiles ->
                    new Thread(()->{
                        if(audioFiles.size()>0) {
                            videoFileAdapter.setItems(audioFiles);
                        }else{
                            finish();
                            return;
                        }
                    }).start();
            audioFileDatabase.audioFileDao().selectByAlbum(album).observe(this, observer);
        }












        searchbuttontop.setOnClickListener(view12 -> startActivity(new Intent(AudioFileActivity.this, SearchActivity.class)));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if(folder!=null && album ==null){
                audioFileDatabase.audioFileDao().selectByFolder(folder).removeObserver(observer);
                audioFileDatabase.audioFileDao().selectByFolder(folder).observe(this, observer);
            }else if(album!=null && folder ==null){
                audioFileDatabase.audioFileDao().selectByAlbum(album).removeObserver( observer);
                audioFileDatabase.audioFileDao().selectByAlbum(album).observe(this, observer);
            }
            swipeRefreshLayout.setRefreshing(false);
        });
    }
}