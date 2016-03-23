package com.vtromeur.jaggersample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vtromeur.jagger.ChatFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().add(R.id.fragment_container, ChatFragment.getInstance("vtrom", "azertyui", "testtest")).commit();

    }

}
