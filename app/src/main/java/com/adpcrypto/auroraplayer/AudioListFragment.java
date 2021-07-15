package com.adpcrypto.auroraplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.adpcrypto.auroraplayer.classes.FolderAlbumSelector;
import com.google.android.material.tabs.TabLayout;


public class AudioListFragment extends Fragment {
    ViewPager viewPager;

    public AudioListFragment() {
    }

  
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main_list, container, false);
        viewPager = root.findViewById(R.id.view_pager);
        TabLayout tabs = root.findViewById(R.id.tabs);

        viewPager.requestLayout();
        final FolderAlbumSelector adapter = new FolderAlbumSelector(getChildFragmentManager(), tabs.getTabCount());

        viewPager.setAdapter(adapter);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return root;
    }
}
/*public AudioListFragment() { }

    Context mcontext;

    List<AudioFile> fileitems;
    List<String> foldernames;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    FolderAdapterAudio videoFileAdapter;
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
            if(!foldernames.contains(f.getFolder())){
                foldernames.add(f.getFolder());
            }
        }

        videoFileAdapter = new FolderAdapterAudio(foldernames,mcontext, decide);
        recyclerView.setAdapter(videoFileAdapter);
        recyclerView.setLayoutManager(new MyLinearLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));



        new Thread(() -> fileitems = audioFileDatabase.audioFileDao().getAudioFiles().getValue()).start();

        Observer observer = (Observer<List<String>>) audioFiles ->
                new Thread(()->videoFileAdapter.setItems(audioFiles)).start();
        audioFileDatabase.audioFileDao().selectAudioFolders().observe(this, observer);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Thread(() -> StartActivity.audioFiles = StartActivity.getAudioFiles(getContext())).start();
            swipeRefreshLayout.setRefreshing(false);
        });

    }*/