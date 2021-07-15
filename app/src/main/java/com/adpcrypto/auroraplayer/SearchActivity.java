package com.adpcrypto.auroraplayer;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.adpcrypto.auroraplayer.classes.AudioFile;
import com.adpcrypto.auroraplayer.classes.InternalStorage;
import com.adpcrypto.auroraplayer.classes.MyLinearLayoutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.adpcrypto.auroraplayer.StartActivity.audioFileDatabase;

public class SearchActivity extends AppCompatActivity {

    EditText search;
    ImageButton back;
    List<AudioFile> search_answer;
    RecyclerView recyclerView;
    AudioFileAdapter searchadapter;
    TextWatcher textWatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();

        search = findViewById(R.id.search_box_);
        search.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);

        back = findViewById(R.id.back_button);
        recyclerView = findViewById(R.id.search_rcv);

            search_answer = new ArrayList<>();
            try {
                search_answer = (List<AudioFile>) InternalStorage.readObject(getApplicationContext(),"my_key");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                search_answer = null;
            }

        searchadapter = new AudioFileAdapter(SearchActivity.this, search_answer,"search");
            recyclerView.setLayoutManager(new MyLinearLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            recyclerView.setAdapter(searchadapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        //new Thread(() -> search_answer = audioFileDatabase.audioFileDao().getAudioFiles().getValue()).start();

        Observer<List<AudioFile>> observer =  audioFiles ->
                new Thread(()->{
                    if(audioFiles.size()>0) {
                        if(search.getText().toString()!=null)
                            searchadapter.setItems(audioFileDatabase.audioFileDao().searchAudioFiles("%"+search.getText().toString()+"%"));
                        else
                            searchadapter.setItems(audioFiles);
                    }
                if(textWatcher!=null){
                    search.removeTextChangedListener(textWatcher);
                    search.addTextChangedListener(textWatcher);
                }}).start();
        audioFileDatabase.audioFileDao().getAudioFiles().observe(this, observer);



        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                new Thread(()->{
                    search_answer = audioFileDatabase.audioFileDao().searchAudioFiles("%"+editable.toString()+"%");
                    searchadapter.setItems(search_answer);
                }).start();
            }
        };
        search.addTextChangedListener(textWatcher);


        back.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    runOnUiThread(() -> back.setBackgroundColor(Color.parseColor("#222222")));
                    return true;
                case MotionEvent.ACTION_UP:
                    runOnUiThread(() -> back.setBackgroundColor(Color.parseColor("#666666")));
                    finish();
                    return false;
            }
            return false;
        });
        search.setOnClickListener(view -> {
            InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm1.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
        });





    }
}