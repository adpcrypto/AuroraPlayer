package com.adpcrypto.auroraplayer.classes;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {AudioFile.class},version = 1,exportSchema = false)
public abstract class AudioFileDatabase extends RoomDatabase {
    private static volatile AudioFileDatabase INSTANCE;
    public abstract AudioFileDao audioFileDao();

    public static AudioFileDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AudioFileDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AudioFileDatabase.class, "audio_file_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
