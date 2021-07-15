package com.adpcrypto.auroraplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{




    BottomNavigationView bottomNavigationView;
    View fragment;
    ImageButton searchButtonTop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);





        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_actionbar);

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.nav_view);
        fragment = findViewById(R.id.nav_host_fragment);



        View view = getSupportActionBar().getCustomView();
        searchButtonTop = view.findViewById(R.id.search_button_top);


        NavHostFragment navHostFragment =       (NavHostFragment)getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView,
                navHostFragment.getNavController());



            searchButtonTop.setOnClickListener(view12 -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));





    }



}


/*SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("SharedPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                boolean isNightMode = sharedPreferences.getBoolean("dark", false);



                                if(!isNightMode){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(settingsButton!=null) {
                                                settingsButton.setBackgroundColor(Color.parseColor("#00999999"));
                                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                            }
                                        }
                                    });
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(settingsButton!=null) {
                                                settingsButton.setBackgroundColor(Color.parseColor("#00999999"));
                                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                            }
                                        }
                                    });
                                }
                                getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
                                recreate();
                                editor.putBoolean("dark",!isNightMode);
                                editor.apply();*/