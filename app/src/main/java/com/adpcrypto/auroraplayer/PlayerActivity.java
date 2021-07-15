package com.adpcrypto.auroraplayer;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.adpcrypto.auroraplayer.classes.PlayerSingleton;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.File;

import static com.adpcrypto.auroraplayer.BackgroundAudioService.player;

public class PlayerActivity extends AppCompatActivity {

    static PlayerView playerView;
    static TextView t1,t2;
    static PlayerControlView playerControlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();
        playerView = findViewById(R.id.player_view);
        playerControlView = findViewById(R.id.playerControlView);
        t1 = findViewById(R.id.audname);
        t2 = findViewById(R.id.artname);

        String where = getIntent().getStringExtra("WHERE");
        String path = getIntent().getStringExtra("PATH");


        if(getIntent().getStringExtra("STOP")!=null){
            if(getIntent().getStringExtra("STOP").equals("STOP")){

                finish();
                return;
            }
        }else if(where==null && path==null) {
            playerView.setPlayer(player);
            playerControlView.setPlayer(player);
            t1.setText(PlayerSingleton.getInstance().audioFile.getFileName());
            if (PlayerSingleton.getInstance().audioFile.getAlbum_artist() == null) {
                t2.setText(PlayerSingleton.getInstance().audioFile.getKey_artist()+"|"+PlayerSingleton.getInstance().audioFile.getAlbum());
            } else {
                t2.setText(PlayerSingleton.getInstance().audioFile.getAlbum_artist()+"|"+PlayerSingleton.getInstance().audioFile.getAlbum());
            }
        }else{
            if(where==null){
                finish();
                return;
            }else{
                if(path!=null){
                    try {
                        new File(path);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                }else{
                    finish();
                    return;
                }
            }
            if(PlayerSingleton.getInstance().audioFile !=null){
                if(PlayerSingleton.getInstance().audioFile.getPath().equals(path)) {
                    playerView.setPlayer(player);
                    playerControlView.setPlayer(player);
                    t1.setText(PlayerSingleton.getInstance().audioFile.getFileName());
                    if (PlayerSingleton.getInstance().audioFile.getAlbum_artist() == null) {
                        t2.setText(PlayerSingleton.getInstance().audioFile.getKey_artist()+"|"+PlayerSingleton.getInstance().audioFile.getAlbum());
                    } else {
                        t2.setText(PlayerSingleton.getInstance().audioFile.getAlbum_artist()+"|"+PlayerSingleton.getInstance().audioFile.getAlbum());
                    }
                }else{
                    Intent i = new Intent(getApplicationContext(), BackgroundAudioService.class);
                    i.putExtra("WHERE", where);
                    i.putExtra("PATH", path);
                    ContextCompat.startForegroundService(getApplicationContext(), i);
                }
            }else{
                Intent i = new Intent(getApplicationContext(), BackgroundAudioService.class);
                i.putExtra("WHERE", where);
                i.putExtra("PATH", path);
                ContextCompat.startForegroundService(getApplicationContext(), i);
            }
        }
    }


}

