package com.adpcrypto.auroraplayer.classes;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "audioFiles")
public class AudioFile implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "path")
    private String path;

    @ColumnInfo(name = "fileName")
    private final String fileName;

    @NonNull
    @ColumnInfo(name = "duration")
    private final String duration;



    @ColumnInfo(name = "size")
    private String size;

    @ColumnInfo(name = "isfav")
    private Boolean isfav;



    @ColumnInfo(name = "folder")
    private String folder;

    @ColumnInfo(name = "album")
    private final String album;

    @ColumnInfo(name = "album_artist")
    private final String album_artist;

    @ColumnInfo(name = "key_artist")
    private final String key_artist;


    @ColumnInfo(name = "title")
    private final String title;

    public AudioFile(@NonNull String id, @NonNull String path, String fileName, @NonNull String duration, String size, Boolean isfav, String folder, String album, String album_artist, String key_artist,  String title) {
        this.id = id;
        this.path = path;
        this.fileName = fileName;
        this.duration = duration;
        this.size = size;
        this.isfav = isfav;
        this.folder = folder;
        this.album = album;
        this.album_artist = album_artist;
        this.key_artist = key_artist;
        this.title = title;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getPath() {
        return path;
    }

    public void setPath(@NonNull String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    @NonNull
    public String getDuration() {
        return duration;
    }





    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


    public Boolean getIsfav() {
        return isfav;
    }

    public void setIsfav(Boolean isfav) {
        this.isfav = isfav;
    }
    public String getFolder() {
        return folder;
    }

    public String getTitle() {
        return title;
    }


    public String getAlbum() {
        return album;
    }


    public String getAlbum_artist() {
        return album_artist;
    }


    public String getKey_artist() {
        return key_artist;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AudioFile audioFile = (AudioFile) o;
        return path.equals(audioFile.path);
    }


}

