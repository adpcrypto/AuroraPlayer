package com.adpcrypto.auroraplayer.classes;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class  AudioDiff extends DiffUtil.Callback {


    final List<AudioFile> a1,a2;
    public AudioDiff(List<AudioFile> a1, List<AudioFile> a2) {
        this.a1= a1;
        this.a2=a2;
    }

    @Override
    public int getOldListSize() {
        return a1.size();
    }

    @Override
    public int getNewListSize() {
        return a2.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return a1.get(oldItemPosition).getId().equals(a2.get(
                newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return (a1.get(oldItemPosition).getPath().equals(a2.get(newItemPosition).getPath()) &&
                a1.get(oldItemPosition).getFileName().equals(a2.get(newItemPosition).getFileName()) &&
                a1.get(oldItemPosition).getDuration().equals(a2.get(newItemPosition).getDuration()) &&
                a1.get(oldItemPosition).getIsfav().equals(a2.get(newItemPosition).getIsfav()));
    }
}
