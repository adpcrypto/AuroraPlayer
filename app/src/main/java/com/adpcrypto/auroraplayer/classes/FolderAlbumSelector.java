package com.adpcrypto.auroraplayer.classes;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.adpcrypto.auroraplayer.AudioAlbumListFrament;
import com.adpcrypto.auroraplayer.AudioFolderListFrament;


public class FolderAlbumSelector extends FragmentPagerAdapter {

    final int totalTabs;

    public FolderAlbumSelector(FragmentManager fm, int totalTabs) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 1)
            return new AudioAlbumListFrament();
        else
            return new AudioFolderListFrament();
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}