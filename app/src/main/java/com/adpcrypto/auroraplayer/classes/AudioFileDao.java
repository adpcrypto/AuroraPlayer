package com.adpcrypto.auroraplayer.classes;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AudioFileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAudioFile(AudioFile videoFile);

    @Update
    void updateAudioFile(AudioFile videoFile);

    @Delete
    void deleteAudioFile(AudioFile videoFile);

    @Query("SELECT * from audioFiles ORDER BY fileName")
    LiveData<List<AudioFile>> getAudioFiles();

    @Query("DELETE FROM audioFiles")
     void clearAudioDatabase();

    @Query("SELECT * FROM audioFiles WHERE path = :givenpath ")
    List<AudioFile> checkforExist(String givenpath);

    @Query("SELECT * FROM audioFiles WHERE isfav = 1 ORDER BY fileName ASC")
     LiveData<List<AudioFile>> getAllFav();


    @Query("SELECT * FROM audioFiles WHERE folder = :givenpath ORDER BY fileName ASC")
    LiveData<List<AudioFile>> selectByFolder(String givenpath);

    @Query("SELECT DISTINCT folder FROM audioFiles ORDER BY folder ASC")
    LiveData<List<String>> selectAudioFolders();

    @Query("SELECT DISTINCT album FROM audioFiles ORDER BY album ASC")
    LiveData<List<String>> selectAudioAlbums();

    @Query("SELECT COUNT(*) FROM audioFiles WHERE folder=:given")
    int selectCountFolder(String given);

    @Query("SELECT * FROM audioFiles WHERE fileName LIKE :givenFileName ORDER BY fileName ASC")
    List<AudioFile> searchAudioFiles(String givenFileName);

    @Query("SELECT * FROM audioFiles WHERE album = :album ORDER BY fileName ASC")
    LiveData<List<AudioFile>> selectByAlbum(String album);

    @Query("SELECT COUNT(*) FROM audioFiles WHERE album=:s")
    int selectCountAlbum(String s);

    @Query("SELECT * FROM audioFiles WHERE album = :givenFileName ORDER BY fileName ASC")
    List<AudioFile> getFileWithAlbum(String givenFileName);

    @Query("SELECT * FROM audioFiles WHERE id = :path ")
    List<AudioFile> checkforExistID(String path);


    @Query("SELECT * FROM audioFiles WHERE folder = :folder ORDER BY fileName ASC")
    List<AudioFile> selectByFolderStatic(String folder);

    @Query("SELECT * FROM audioFiles WHERE album = :folder ORDER BY fileName ASC")
    List<AudioFile> selectByAlbumStatic(String folder);

    @Query("SELECT * FROM audioFiles WHERE isfav = 1 ORDER BY fileName ASC")
    List<AudioFile> getAllFavStatic();

    @Query("SELECT * from audioFiles ORDER BY fileName")
    List<AudioFile> getAudioFilesStatic();
}
