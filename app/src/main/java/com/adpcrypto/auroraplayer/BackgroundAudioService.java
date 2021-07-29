package com.adpcrypto.auroraplayer;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.adpcrypto.auroraplayer.classes.PlayerSingleton;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.adpcrypto.auroraplayer.PlayerActivity.playerControlView;
import static com.adpcrypto.auroraplayer.PlayerActivity.playerView;
import static com.adpcrypto.auroraplayer.StartActivity.audioFileDatabase;

public class BackgroundAudioService extends Service {


    static SimpleExoPlayer player;
    Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        player = new SimpleExoPlayer.Builder(context).build();
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(context, "My_channel_id", R.string.app_name, R.string.app_name, 123, mediaDescriptionAdapter, new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                stopSelf();
            }

            @Override
            public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                if(ongoing)
                    startForeground(notificationId,notification);
            }
        });
        player.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(MediaItem mediaItem, int reason) {
                int pos = player.getCurrentWindowIndex();
                PlayerSingleton.getInstance().audioFile = PlayerSingleton.getInstance().playingList.get(pos);
                PlayerActivity.t1.setText(PlayerSingleton.getInstance().audioFile.getFileName());
                if (PlayerSingleton.getInstance().audioFile.getAlbum_artist() == null) {
                    PlayerActivity.t2.setText(PlayerSingleton.getInstance().audioFile.getKey_artist()+"|"+PlayerSingleton.getInstance().audioFile.getAlbum());
                } else {
                    PlayerActivity.t2.setText(PlayerSingleton.getInstance().audioFile.getAlbum_artist()+"|"+PlayerSingleton.getInstance().audioFile.getAlbum());
                }
            }


            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if(!isPlaying){
                    stopForeground(false);
                }else{
                    playerNotificationManager.setPlayer(player);
                    player.play();
                }
            }
        });
        /*player.addListener(new Player.EventListener() {

            @Override
            public void onPlaybackStateChanged(int state) {

            }
            
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {

            }
        });*/
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        playerNotificationManager.setPlayer(null);
        player.release();
        player = null;
        PlayerSingleton.getInstance().audioFile = null;
        PlayerSingleton.getInstance().playingList = new ArrayList<>();
        new Thread(()->{
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses != null){
                final String packageName = context.getPackageName();
                for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                    if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                        Intent i = new Intent(this, PlayerActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("STOP", "STOP");
                        context.startActivity(i);
                    }
                }
            }
        }).start();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private PlayerNotificationManager.MediaDescriptionAdapter mediaDescriptionAdapter = new PlayerNotificationManager.MediaDescriptionAdapter() {
        @Override
        public String getCurrentSubText(Player player) {
            return " ";
        }

        @Override
        public String getCurrentContentTitle(Player player) {
            if(PlayerSingleton.getInstance().playingList.size()>player.getCurrentWindowIndex())
                return PlayerSingleton.getInstance().playingList.get(player.getCurrentWindowIndex()).getTitle();
            else
                return "";
        }

        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            Intent intentForeground = new Intent(context, PlayerActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            return PendingIntent.getActivity(getApplicationContext(), 0, intentForeground, 0);
        }

        @Override
        public String getCurrentContentText(Player player) {
            if(PlayerSingleton.getInstance().playingList.size()>player.getCurrentWindowIndex())
                return PlayerSingleton.getInstance().playingList.get(player.getCurrentWindowIndex()).getAlbum();
            else
                return "";
        }

        @Override
        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
            if(PlayerSingleton.getInstance().playingList.size()>player.getCurrentWindowIndex())
                return coverpicture(PlayerSingleton.getInstance().playingList.get(player.getCurrentWindowIndex()).getPath());
            else
                return null;
        }
    };


    PlayerNotificationManager playerNotificationManager;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        String where = intent.getStringExtra("WHERE");
        String path = intent.getStringExtra("PATH");

        if(PlayerSingleton.getInstance().audioFile!=null) {
            if (!path.equals(PlayerSingleton.getInstance().audioFile.getPath())) {
                player.pause();
            }
        }


        new StartOperation(where,path).execute();





        return START_STICKY;
    }

    static Bitmap coverpicture(String path) {
        MediaMetadataRetriever mr;
        byte[] byte1 = new byte[1];

        mr = new MediaMetadataRetriever();
        mr.setDataSource(path);
        try {
            byte1 = mr.getEmbeddedPicture();
            mr.release();
        }catch (Exception e){
            e.printStackTrace();
        }



        if(byte1 != null) {
            return BitmapFactory.decodeByteArray(byte1, 0, byte1.length);
        }
        else {
            return null;
        }

    }


    public class StartOperation extends AsyncTask{

        String where,path;
        int pos;
        ConcatenatingMediaSource concatenatingMediaSource;

        public StartOperation(String where,String path){
            this.where = where;
            this.path = path;
        }

        @Override
        protected Object doInBackground(Object... objects) {

            PlayerSingleton playerSingleton = PlayerSingleton.getInstance();

            playerSingleton.playingList = new ArrayList<>();
            playerSingleton.audioFile = null;

            if(where!=null&&path!=null){
                switch (where) {
                    case "search":
                        playerSingleton.playingList = audioFileDatabase.audioFileDao().getAudioFilesStatic();
                        playerSingleton.audioFile = audioFileDatabase.audioFileDao().checkforExist(path).get(0);
                        pos = playerSingleton.playingList.indexOf(playerSingleton.audioFile);
                        break;
                    case "fav":
                        playerSingleton.playingList = audioFileDatabase.audioFileDao().getAllFavStatic();
                        playerSingleton.audioFile = audioFileDatabase.audioFileDao().checkforExist(path).get(0);
                        pos = playerSingleton.playingList.indexOf(playerSingleton.audioFile);
                        break;
                    case "folder":
                        playerSingleton.audioFile = audioFileDatabase.audioFileDao().checkforExist(path).get(0);
                        playerSingleton.playingList = audioFileDatabase.audioFileDao().selectByFolderStatic(playerSingleton.audioFile.getFolder());
                        pos = playerSingleton.playingList.indexOf(playerSingleton.audioFile);
                        break;
                    case "album":
                        playerSingleton.audioFile = audioFileDatabase.audioFileDao().checkforExist(path).get(0);
                        playerSingleton.playingList = audioFileDatabase.audioFileDao().selectByAlbumStatic(playerSingleton.audioFile.getAlbum());
                        pos = playerSingleton.playingList.indexOf(playerSingleton.audioFile);
                        break;
                    default:
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show());

                        stopSelf();
                }
            }


            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                    context, Util.getUserAgent(context, getString(R.string.app_name)));

            concatenatingMediaSource = new ConcatenatingMediaSource();
            for (int i = 0; i < playerSingleton.playingList.size(); i++) {
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(playerSingleton.playingList.get(i).getPath()));
                MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
                //previous  new ExtractorMediaSource.Factory
                ///CHECK
                concatenatingMediaSource.addMediaSource(mediaSource);
            }



            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            player.setMediaSource(concatenatingMediaSource);
            player.prepare();
            player.seekTo(pos,0);
            Log.e("TOT"+PlayerSingleton.getInstance().playingList.size(),"pos"+pos);
            playerNotificationManager.setPlayer(player);
            playerView.setPlayer(player);
            playerControlView.setPlayer(player);
            PlayerActivity.t1.setText(PlayerSingleton.getInstance().audioFile.getFileName());
            if (PlayerSingleton.getInstance().audioFile.getAlbum_artist() == null) {
                PlayerActivity.t2.setText(PlayerSingleton.getInstance().audioFile.getKey_artist()+"|"+PlayerSingleton.getInstance().audioFile.getAlbum());
            } else {
                PlayerActivity.t2.setText(PlayerSingleton.getInstance().audioFile.getAlbum_artist()+"|"+PlayerSingleton.getInstance().audioFile.getAlbum());
            }
            player.play();
        }
    }

}






