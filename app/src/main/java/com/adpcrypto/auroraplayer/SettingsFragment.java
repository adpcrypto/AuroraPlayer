package com.adpcrypto.auroraplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {



    SwitchCompat aSwitch;
    Context mContext;
    public SettingsFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        aSwitch = view.findViewById(R.id.dark_mode);


        loadForDark();

        aSwitch.setOnClickListener(view1 -> {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("SharedPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            boolean isNightMode = sharedPreferences.getBoolean("dark", true);



            if(!isNightMode){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            editor.putBoolean("dark",!isNightMode);
            editor.apply();

            getActivity().recreate();

        });

    }

    private void loadForDark() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        boolean isNightMode = sharedPreferences.getBoolean("dark", true);


        aSwitch.setChecked(isNightMode);

    }


}