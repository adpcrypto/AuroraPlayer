package com.adpcrypto.auroraplayer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.adpcrypto.auroraplayer.classes.AudioFile;
import com.adpcrypto.auroraplayer.classes.InternalStorage;
import com.adpcrypto.auroraplayer.classes.MyGridLayoutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.adpcrypto.auroraplayer.StartActivity.audioFileDatabase;

public class AudioAlbumListFrament extends Fragment {

    Observer observer;
    public AudioAlbumListFrament(){
    }

    Context mcontext;

    List<AudioFile> fileitems;
    List<String> foldernames;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    FolderAdapterAlbum videoFileAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mcontext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = view.findViewById(R.id.audiio_swipe_ref);
        recyclerView = view.findViewById(R.id.audio_folder_rcv);

        foldernames = new ArrayList<>();

            try {
                fileitems = (List<AudioFile>) InternalStorage.readObject(getContext(),"my_key");
            } catch (IOException e) {
                e.printStackTrace();
                fileitems = new ArrayList<>();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                fileitems = new ArrayList<>();
            }

            for(AudioFile f:fileitems){
                if(!foldernames.contains(f.getAlbum())){
                    foldernames.add(f.getAlbum());
                }
            }




        videoFileAdapter = new FolderAdapterAlbum(foldernames,mcontext);
        recyclerView.setAdapter(videoFileAdapter);

        recyclerView.setLayoutManager(new MyGridLayoutManager(mcontext,2));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);





            observer = (Observer<List<String>>) audioFiles1 ->
                    AsyncTask.execute(()->{
                        if(audioFiles1.size()>0)
                            videoFileAdapter.setItems(audioFiles1);
                    });
            audioFileDatabase.audioFileDao().selectAudioAlbums().observe(getViewLifecycleOwner(), observer);



        swipeRefreshLayout.setOnRefreshListener(() -> {

                audioFileDatabase.audioFileDao().selectAudioAlbums().removeObserver(observer);
                audioFileDatabase.audioFileDao().selectAudioAlbums().observe(getViewLifecycleOwner(),observer);

            swipeRefreshLayout.setRefreshing(false);
        });

    }
}
