package com.adpcrypto.auroraplayer;

import android.content.Context;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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


public class FavouriteListFragment extends Fragment {

    RecyclerView recyclerView;
    AudioFileAdapter videoFileAdapter;
    Observer observer;
    SwipeRefreshLayout swipeRefreshLayout;
    List<AudioFile> fileitems;
    Context mcontext;

    TextWatcher textWatcher;
    public FavouriteListFragment() { }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mcontext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_file, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = view.findViewById(R.id.swipe_video_file);


        recyclerView = view.findViewById(R.id.file_rcv);

        List<AudioFile> torem = new ArrayList<>();
        try {
            fileitems = (List<AudioFile>) InternalStorage.readObject(mcontext,"my_key");
            for(AudioFile aud:fileitems){
                if(!aud.getIsfav()){
                    torem.add(aud);
                }
            }
            fileitems.removeAll(torem);
        } catch (IOException e) {
            fileitems = new ArrayList<>();
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            fileitems = new ArrayList<>();
            e.printStackTrace();
        }

        videoFileAdapter = new AudioFileAdapter(mcontext, fileitems,"fav");
        recyclerView.setAdapter(videoFileAdapter);
        recyclerView.setLayoutManager(new MyLinearLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);






        //new Thread(() -> fileitems = audioFileDatabase.audioFileDao().getAllFav().getValue()).start();

        observer = (Observer<List<AudioFile>>) audioFiles ->
                new Thread(()->{
                    videoFileAdapter.setItems(audioFiles);
                }).start();
        audioFileDatabase.audioFileDao().getAllFav().observe(getViewLifecycleOwner(), observer);







        swipeRefreshLayout.setOnRefreshListener(() ->
        {
            audioFileDatabase.audioFileDao().getAllFav().removeObserver( observer);
            audioFileDatabase.audioFileDao().getAllFav().observe(getViewLifecycleOwner(), observer);
            swipeRefreshLayout.setRefreshing(false);
        });

    }


}