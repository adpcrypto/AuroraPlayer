package com.adpcrypto.auroraplayer.classes;

import java.util.ArrayList;
import java.util.List;

public class PlayerSingleton  {

    private static PlayerSingleton mInstance;
    public List<AudioFile> playingList;
    public AudioFile audioFile;

    public static PlayerSingleton getInstance() {
        if(mInstance == null)
            mInstance = new PlayerSingleton();
        return mInstance;
    }

    private PlayerSingleton() {
        playingList = new ArrayList<>();
    }


}