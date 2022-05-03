package com.example.mobilehw2_nivshalom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.mobilehw2_nivshalom.activities.GameActivity;
import com.example.mobilehw2_nivshalom.activities.GameSensorActivity;
import com.example.mobilehw2_nivshalom.activities.LeaderBoardsActivity;
import com.example.mobilehw2_nivshalom.logic.GPS;
import com.example.mobilehw2_nivshalom.logic.GameUtility;

public class MainActivity extends AppCompatActivity {

    public static GPS gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        gps = new GPS(this);

        Button start_normal = findViewById(R.id.start_normal_btn);
        Button sensor_normal = findViewById(R.id.start_sensor_btn);
        Button leaderboards = findViewById(R.id.leaderboards_btn);

        start_normal.setOnClickListener((event) -> GameUtility.GoToActivity(this, GameActivity.class));
        sensor_normal.setOnClickListener((event) -> GameUtility.GoToActivity(this, GameSensorActivity.class));
        leaderboards.setOnClickListener((event) -> GameUtility.GoToActivity(this, LeaderBoardsActivity.class));
    }

}